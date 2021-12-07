# ScaleUp Backend Guide

Little guide for the ScaleUp Backend

___

## Users

API Requests and responses for the user controller

### API Requests

<table>
  <tr>
   <td> HTTP </td> <td> URL </td> <td> Body </td>
  </tr>
  <tr>
  <td> POST </td>
  <td> http://localhost:8080/api/v1/user/ </td>
  <td>

  ```json
  {
      "id": "15787aab-9ec8-48c8-9535-ba1ff585b16a",
      "username": "horst",
      "leagues": {}
  }
  ```

  </td>
  <tr>
  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/user/all </td>
  <td>

  `-`

  </td>
  </tr>
  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/user/all/{id} </td>
  <td>

  `-`

  </td>
  </tr>
  <tr>
  <td> PUT </td>
  <td> http://localhost:8080/api/v1/user/{id}/leagues </td>
  <td>

  ```json
  {
      "leagueId": "1d701622-4e4c-481e-84ad-02b8aec21136",
      "leagueCode": "0000"
  }
  ```

  </td>
  <tr>
</table>

### API Response

<table>
  <tr>
  <td> Status </td> <td> Response </td>
  </tr>
  <tr>
  <td> 200 </td>
  <td>

  ```json
  {
      "id": "15787aab-9ec8-48c8-9535-ba1ff585b16a",
      "username": "user1",
      "leagues": {}
  }
  ```

  </td>
  </tr>
  <tr>
  <td> 204 </td>
  <td>

No users saved in DB

  </td>
  </tr>
  <tr>
  <td> 404 </td>
  <td>

User could not be found under this id or username

  </td>
  </tr>
  <tr>
  <td> 409 </td>
  <td>

User with this username already saved in DB

  </td>
  </tr>
  <tr>
  <td> 400 </td>
  <td>

**Bad Request**

  </td>
  </tr>
</table>

___

## Leagues

API Requests and responses for the leagues controller\
**Not implemented yet!**

### API Requests

<table>
  <tr>
   <td> HTTP </td> <td> URL </td> <td> Body </td>
  </tr>
  <tr>
  <td> POST </td>
  <td> http://localhost:8080/api/v1/league/ </td>
  <td>

  ```json
  {
      "userId": "ba7159b0-10e8-4141-b085-4d1060bd739c",
      "leagueId": "1d701622-4e4c-481e-84ad-02b8aec21136",
      "leagueName": "Best league ever",
      "code": "0000",
      "startBudget": 10000,
      "transactionCost": 5,
      "stockAmount": 200,
      "probability":
      {
          "Technology": 0.5,
          "Communication Services": 0.5,
          "...": "..."
      }
  }
  ```

  </td>
  </tr>
  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/league/{id} </td>
  <td>

  `-`

  </td>
  </tr>
  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/league/all </td>
  <td>

  `-`

  </td>
  </tr>
  <tr>
  <td> PUT </td>
  <td> tbc. </td>
  <td>

  `-`

  </td>
  </tr>
</table>

### API Response

<table>
  <tr>
  <td> Status </td> <td> Response </td>
  </tr>
  <tr>
  <td> 200 </td>
  <td>

  **OK**

  </td>
  </tr>
  <tr>
  <td> 204 </td>
  <td>

No leagues saved in DB

  </td>
  </tr>
  <tr>
  <td> 404 </td>
  <td>

League could not be found under this id

  </td>
  </tr>
  <tr>
  <td> 409 </td>
  <td>

Either this league id does already exist or the user does not exist in the DB

  </td>
  </tr>
  <tr>
  <td> 400 </td>
  <td>

**Bad request**

  </td>
  </tr>
</table>
