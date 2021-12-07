# ScaleUp Backend Guide

Little guide for the ScaleUp Backend

___

## Users

API Requests and responses for the user controller.

_Always look at the message in the JSON response for further information if you get an error._

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
      "username": "horst"
  }
  ```

  </td>
  </tr>
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
  <td> http://localhost:8080/api/v1/user/{id} </td>
  <td>

  ```json
  {
      "id": "15787aab-9ec8-48c8-9535-ba1ff585b16a",
      "username": "hans"
  }
  ```

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

  ```json
  {
      "id": "15787aab-9ec8-48c8-9535-ba1ff585b16a",
      "username": "horst"
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
  <td> http://localhost:8080/api/v1/ </td>
  <td>

  `-`

  </td>
  <tr>
  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/ </td>
  <td>

  `-`

  </td>
  </tr>
  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1 </td>
  <td>

  `-`

  </td>
  </tr>
  <tr>
  <td> PUT </td>
  <td> http://localhost:8080/api/v1/ </td>
  <td>

  `-`

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

  **Markdown** _here_. (Blank lines needed before and after!)

  </td>
  </tr>
  <tr>
  <td> 204 </td>
  <td>

  `No content`

  </td>
  </tr>
  <tr>
  <td> 400 </td>
  <td>

  **Markdown** _here_. (Blank lines needed before and after!)

  </td>
  <tr>
</table>


___

## Stocks

API Requests and responses for the stock controller\

### API Requests

<table>
  <tr>
   <td> HTTP </td> <td> URL </td> <td> Body </td>
  </tr>
  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/stock/{symbol} </td>
  <td>

`-`

  </td>
  </tr>
  <tr>
  <td> GET </td>
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

```json
{
    "symbol": "BTC-EUR",
    "lastUpdated": "2021-12-06T19:05:29.000+00:00",
    "price": 43411.125,
    "dayOpen": 43632.46,
    "previousClose": 43632.46,
    "dayHigh": 43644.844,
    "dayLow": 41835.867,
    "fiftyTwoHigh": 59496.15,
    "fiftyTwoLow": 14539.374,
    "volume": 3.15467428E10,
    "stockType": "CRYPTOCURRENCY",
    "sector": "Blockchain"
}
```

  </td>
  </tr>
  <tr>
  <td> 404 </td>
  <td>

Stock with this symbol could not be found in the DB

  </td>
  </tr>
  <tr>
  <td> 400 </td>
  <td>

**Bad Request**

  </td>
  </tr>
</table>
