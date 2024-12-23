### Making configurable base path

- Base path can be added in postman environment file or in postman.
- `Resource-Proxy-Server-Consumer-APIs.postman_environment.json` has **values** array that has fields named **basePath**
  whose **value** is currently set to `ngsi-ld/v1`, **dxAuthBasePath** with value `auth/v1`.
- These value(s) could be changed according to the deployment and then the collection with
  the `Resource-Proxy-Server-Consumer-APIs.postman_environment.json` file can be uploaded to Postman
- For the changing the **basePath**, **dxAuthBasePath** value in postman after importing the collection and environment
  files, locate `RS Environment` from **Environments** in sidebar of Postman application.
- To know more about Postman environments,
  refer : [postman environments](https://learning.postman.com/docs/sending-requests/managing-environments/)
- The **CURRENT VALUE** of the variable could be changed

| Key                  |            Value Example             | Description                                                                                                                                                                                                                              |
|:---------------------|:------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| baseUrl              |          https://example.in          | URL of DX Resource Server                                                                                                                                                                                                                |
| basePath             |              ngsi-ld/v1              | Configurable base path of DX Resource Server                                                                                                                                                                                             |
| providerClientId     | 3b4f0ea5-7fbe-4b93-b449-39b845e0e042 | - Keycloak generated client ID of the provider that is generated after registration with DX platform as a provider<br/>  - To get the token for creating, getting, deleting policy and approving, rejecting, fetching access request     |
| providerClientSecret | c8a74cf0-f6f6-4dfe-8b93-1ceaf9fa6614 | - Keycloak generated client secret of the provider that is generated after registration with DX platform as a provider<br/>  - To get the token for creating, getting, deleting policy and approving, rejecting, fetching access request |
| clientID             | ca84ffbe-1d53-4268-959c-2335f7b8ee09 | - Keycloak generated client ID of the provider that is generated after registration with DX platform as a consumer <br/>  - To get the token for getting policy and creating, withdrawing, fetching access request                       |
| clientSecret         | 8fadd5c2-c766-4837-bdb1-26c193be4b2c | - Keycloak generated client secret of the provider that is generated after registration with DX platform as a consumer <br/>  - To get the token for getting policy and creating, withdrawing, fetching access request                   |
| auth-url             |          authvertx.iudx.io           | URL of DX AAA to get tokens                                                                                                                                                                                                              |
| delegationId         | 5b8e6ada-f409-4e12-9648-cdf6963f4066 | - Keycloak generated delegation ID of the provider delegate <br/>  - To get the token for creating, getting, deleting policy and approving, rejecting and fetching access request                                                        |
| dxAuthBasePath       |               /auth/v1               | Configurable DX AAA base path                                                                                                                                                                                                            |
