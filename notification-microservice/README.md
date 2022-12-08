# Notification microservice
This microservice is responsible for notifying a user upon:
- acceptance
- rejection
- withdrawal
- being kicked from the activity
- the activity they were signed-up for being deleted

# Endpoints
- POST /notify
- - the authorization header needs to contain a jwt token
- - the body needs to contain a json of the form: {\
  "status": "REJECTED",\
  "email": "aojica65@gmail.com"\
  }

Upon receiving the needed information through a POST request at /notify this microservice then sends an email to the specified address containing the status.