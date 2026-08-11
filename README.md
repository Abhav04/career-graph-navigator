# career-graph-navigator

# Career Path & Learning Map Explorer

A graph-database-backed application that helps people navigate from where they are 
to where they want to be professionally — answering questions like *"I know Java and 
SQL, what should I learn next to become a Backend Engineer?"* and *"What's the shortest 
learning path to becoming an AI Engineer?"*

**Live demo:** https://career-graph-navigator.vercel.app/
**Backend API:** https://career-graph-navigator.onrender.com

## Why a graph database?

Career progression is fundamentally a network problem, not a table-lookup problem. 
A person's skills, a job's requirements, and the prerequisite relationships between 
skills form a genuine graph — and the questions people actually ask about their 
career ("what's next," "what's the fastest path") are graph traversal questions, 
not row lookups.

A relational schema can represent *that* a skill requires another skill (a join 
table), but it struggles with the questions that make this data useful:

- **Weighted shortest-path queries** — finding the lowest-effort learning route from 
  someone's current skills to a target job's requirements means traversing a 
  variable-depth chain of prerequisites, accumulating a cost (estimated learning 
  hours) along the way. In SQL, this requires a recursive CTE with manual cost 
  accumulation logic that becomes unreadable past 2-3 levels of depth and performs 
  poorly as the chain grows. In Cypher, this is a native traversal over weighted 
  relationships — the kind of query the database is built for.

- **Negative pattern matching** — "find skills this job requires that this person 
  does NOT know" is a natural `WHERE NOT (pattern)` clause in Cypher. In SQL, the 
  equivalent is a `NOT EXISTS` correlated subquery or a `LEFT JOIN ... WHERE IS NULL` 
  anti-join — functionally possible, but noticeably more awkward to write and reason 
  about, especially once combined with a second traversal (as in our gap-analysis 
  query, which also computes hop-distance for each missing skill).

- **Evidence beyond self-reported data** — a person's demonstrated skills (via 
  projects they've built) may differ from what they've explicitly listed. Traversing 
  `Person → BUILT → Project → DEMONSTRATES → Skill` alongside `Person → KNOWS → Skill` 
  and reconciling the two is a multi-hop pattern match that's natural in a graph and 
  would require multiple joins and careful deduplication logic in a relational schema.

None of these queries are *impossible* in SQL — but a graph database lets us express 
them the way we actually think about the problem: as paths and connections, not joins.

## Data Model

### Nodes
| Label | Key Properties |
|---|---|
| `Person` | name, currentRole |
| `Skill` | name, category |
| `Job` | title, seniorityLevel |
| `Company` | name |
| `Project` | title, description |
| `LearningResource` | title, url, resourceType |
| `Industry` | name |

### Relationships
| Relationship | Direction | Properties | Meaning |
|---|---|---|---|
| `KNOWS` | Person → Skill | proficiency | Self-reported skill knowledge |
| `PREREQUISITE_OF` | Skill → Skill | strength | Skill dependency chain |
| `REQUIRES` | Job → Skill | importance | Skills a job needs, weighted by importance |
| `LEARNED_VIA` | Skill → LearningResource | estHours, difficulty | How to learn a skill, with cost |
| `BUILT` | Person → Project | — | Projects a person has built |
| `DEMONSTRATES` | Project → Skill | — | Skills evidenced by a project |
| `AT` | Job → Company | — | Where a job is located |
| `IN_INDUSTRY` | Company → Industry | — | Company's industry |

### Diagram

![Graph data model]
https://drive.google.com/file/d/1FzRLbfqg0OZrjY2NmmnM0mHYkdazu18b/view?usp=drive_link

*Rendered directly from the seeded CognoDB instance via its built-in graph browser, 
showing the actual prerequisite chains, job requirements, and person/project/skill 
relationships in the live dataset.*

## Setup & Run Instructions

### Prerequisites
- JDK 21
- Maven 3.9+
- Node.js 20+
- A CognoDB Cloud account (free tier, no credit card required)

### 1. Create your CognoDB instance

1. Sign up at [console.cognodb.com/signup](https://console.cognodb.com/signup)
2. Create a free (c0) instance and pick a region
3. Save the connection URI (`bolt+s://<instance-id>.databases.cognodb.cloud`), 
   username (`cognodb`), and the generated password — **the password is shown only 
   once.**

### 2. Seed the database

Open CognoDB Cloud's built-in query browser for your instance and run the contents 
of [`backend/src/main/resources/seed/seed.cypher`](./backend/src/main/resources/seed/seed.cypher) 
(paste the full script and execute — it's idempotent, safe to re-run).

Verify the seed worked:
```cypher
MATCH (n) RETURN labels(n)[0] AS label, count(*) AS count ORDER BY label;
```
Expected: Company: 4, Industry: 2, Job: 4, LearningResource: 11, Person: 4, 
Project: 3, Skill: 11.

### 3. Configure environment variables

In `backend/`, create a `.env` file (never committed — see `.gitignore`):
COGNODB_URI=bolt+s://<your-instance-id>.databases.cognodb.cloud
COGNODB_USER=cognodb
COGNODB_PASSWORD=<your-password>

### 4. Run the backend

```bash
cd backend
mvn spring-boot:run
```
Confirm it's up: `curl http://localhost:8080/api/health` should return 
`{"status": "connected"}`.

### 5. Run the frontend

```bash
cd frontend
npm install
npm run dev
```
Open the URL Vite prints (typically `http://localhost:5173`).


*Note: the backend is hosted on Render's free tier, which spins down after 
inactivity — the first request after idle time may take 30-60 seconds to respond 
while the service wakes up.*

## Main Queries

### 1. Skill Gap Analysis
*"I know Java and SQL. What should I learn next to become a Backend Engineer?"*

```cypher
MATCH (person:Person {name: $personName})-[:KNOWS]->(knownSkill:Skill)
MATCH (job:Job {title: $jobTitle})-[req:REQUIRES]->(missingSkill:Skill)
WHERE NOT (person)-[:KNOWS]->(missingSkill)
OPTIONAL MATCH path = shortestPath(
    (knownSkill)-[:PREREQUISITE_OF*1..4]->(missingSkill)
)
RETURN missingSkill.name AS skillToLearn,
       req.importance AS importance,
       CASE WHEN path IS NULL THEN 999 ELSE length(path) END AS hopsFromKnownSkill
ORDER BY hopsFromKnownSkill ASC, importance DESC
```

Finds every skill a target job requires that the person doesn't already know, then 
ranks them by how close they are (in prerequisite hops) to something the person 
already knows — so the recommendation is "learn what builds naturally on what you 
already have," not just an unordered list of gaps. The `WHERE NOT` clause is a 
negative pattern match — genuinely awkward to express in SQL, natural in Cypher.

### 2. Weighted Shortest Learning Path
*"Show me the shortest learning path to become an AI Engineer."*

```cypher
MATCH (person:Person {name: $personName})-[:KNOWS]->(startSkill:Skill)
MATCH (job:Job {title: $jobTitle})-[:REQUIRES]->(targetSkill:Skill)
WHERE NOT (person)-[:KNOWS]->(targetSkill)
CALL {
    WITH startSkill, targetSkill
    MATCH path = (startSkill)-[rels:PREREQUISITE_OF*1..6]->(targetSkill)
    RETURN path, /* cost accumulated from LEARNED_VIA.estHours along the path */
    ORDER BY estimatedHours ASC LIMIT 1
}
RETURN targetSkill.name AS targetSkill,
       [n IN nodes(path) | n.name] AS learningPath,
       estimatedHours
ORDER BY estimatedHours ASC
```

For each missing skill, finds the lowest-total-effort path through the prerequisite 
graph, weighting each step by its estimated learning hours (from `LEARNED_VIA`). This 
is a genuine weighted-graph shortest-path problem — the kind of query a relational 
recursive CTE can technically express but performs and reads far worse than Cypher's 
native path traversal.

### Parameterization
All queries use `$paramName` placeholders bound via Spring Data Neo4j's `@Param`, 
never string concatenation — preventing Cypher injection the same way prepared 
statements prevent SQL injection.

<img width="2940" height="1554" alt="image" src="https://github.com/user-attachments/assets/08c6e5e3-51d8-4e5c-a240-d5b76cbbf65a" />
<img width="2940" height="1542" alt="image" src="https://github.com/user-attachments/assets/5bf84de8-af80-4724-ab43-16d931e90842" />
