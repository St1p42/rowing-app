# Authentication microservice
The `authentication-microservice` is responsible for registering new users and authenticating current ones. After successful authentication, this microservice will provide a JWT token which can be used to bypass the security on the `example-microservice`. This token contains the *Username* of the user that authenticated. If your scenario includes different roles, these will have to be added to the authentication-microservice and to the JWT token. To do this, you will have to:
- Add a concept of roles to the `AppUser`
- Add the roles to the `UserDetails` in `JwtUserDetailsService`
- Add the roles as claims to the JWT token in `JwtTokenGenerator`

## Running the authentication microservice

You can run the two microservices individually by starting the Spring applications. Then, you can use *Postman* to perform the different requests:


Register:
![image](../instructions/register.png)

Authenticate:
![image](../instructions/authenticate.png)

