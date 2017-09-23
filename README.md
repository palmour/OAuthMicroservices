# OAuthMicroservices
This is similar to the basic OAuth project we did before, only this time we move all the authentication logic to the zuul server. Since all requests are routed through the zuul proxy, each service in the application has access to the OAuth2 authentication details once a user logs in. Feel free to read through and make sure this works on your machine. 

My next goal is to figure out how to prevent associates from accessing certain endpoints (so that they're only accessible to managers/admins).
