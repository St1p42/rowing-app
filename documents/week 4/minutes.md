# Second Meeting Minutes
## Meeting Information
**Meeting Date/Time:** 06.12.2022, 14:45-15:15  
**Meeting Purpose:**  Review and Planning  
**Meeting Location:** DW PC-Hall 1, Cubicle 7  
**Note Taker:** Alexandru Ojica  

## Attendees
People who attended:
- Alexandru-Gabriel Cojocaru
- Efe Unluyurt
- Bogdan-Andrei Bancuta
- Alexandru-Nicolae Ojica
- Joyce Sung
- Khalit Gulamov

<!---## Agenda Items

Item | Description
---- | ----
Agenda Item 1 | • <br>• <br>• <br>• <br>•-->

## Questions for the TA
| Question                                                                                 | Answer                                                             |
|------------------------------------------------------------------------------------------|--------------------------------------------------------------------|
| Do we need to create wrapper classes for every attribute that is stored in the database? | There is no need to create wrapper classes for every attribute.    |
| How to integrate hexagonal microservice in our microservice?                             | Codrin will come back later with the answer.                       |

## Observations from the TA
- They run git inspector weekly on main and we should have everything merged in main by sunday
- Meeting in week 5 is not mandatory for those who take the ADS exam
- The rubric contains entry about discrimination and inclusion, distribute workload equally
- We need tests, integration testing(between microservices), functional tests from high-level documents, boundary testing, test as we implement, junit is best with mockito
- Tag Codrin for deliverable 1: to tag select specific commit, in right header options create tag, create tag for merge request
- We should do integration testing for full mutation score.
- Explain communication in assignment doc, why sync/async, no advantage to kafka grade wise.

## Feedback for assignment:
- The uml looks nice, almost what was intended to be done
- The microservices should have the components label not service in uml diagram, and model controllers and services inside microservices

## How to do activity microservice:
- Create entity for each activity tab and have 2 different repositories, scalability doesn't apply to this
- For notifications send emails

## Action Items
| Done?    | Item                                                                                             | Responsible | Due Date   |
|----------|--------------------------------------------------------------------------------------------------| ---- |------------|
| Not Done | Start writing functionality, basic functions of each microservice, endpoints                     | Everyone | 11.12.2022 |
| Not Done | Later in the week start implementing communication between microservices maybe with kafka, async | Everyone | 11.12.2022 |
## Other Notes & Information
N/A


