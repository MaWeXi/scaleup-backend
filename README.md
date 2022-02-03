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
      "username": "horst",
      "leagues": {}
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
  "username": "horst",
  "leagues": {}
}
   ```

  </td>
  <tr>

  <tr>
  <td> PUT </td>
  <td> http://localhost:8080/api/v1/user/join-league/{id} </td>
  <td>

  ```json
  {
      "leagueId": "1d701622-4e4c-481e-84ad-02b8aec21136",
      "leagueCode": "0000"
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

API Requests and responses for the leagues controller

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
      "leagueName": "Best league ever",
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
  <td> GET </td>
  <td> http://localhost:8080/api/v1/league/leaderboard/{id} </td>
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


___

## Stocks

API Requests and responses for the stock controller\
askPrice returns current askPrice and its development in percent to the value when the day started\
bidPrice returns current bidPrice and its development in percent to the value when the day started\


### API Requests

<table>
  <tr>
   <td> HTTP </td> <td> URL </td> <td> Body </td>
  </tr>

  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/stock/{symbol}
       <br>
       http://localhost:8080/api/v1/stock/{symbol}?interval=day</td>
  <td> Optional URL Parameters:
       <br>
       interval = day, week, month</td>
  </tr>

<tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/stock/askPrice/{Symbol} </td>
  <td>

`-`

  </td>
  </tr>

<tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/stock/bidPrice/{symbol} </td>
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
    "symbol": "BMW.DE",
    "name": "Bayerische Motorenwerke",
    "isin": "DE0005190003",
    "wkn": "519000",
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
    "sector": "Blockchain",
    "stockHistory": [
        {
          "date": "2021-02-03T16:00:00.000+00:00",
          "close": 133.750000000000000000
        },
        {
          "date": "2021-02-02T09:30:00.000+00:00",
          "close": 135.650000000000000000
        }
    ]  
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

___

## Markets

API Requests and responses for the market controller\
market returns complete market for league (here we need to add information that you want to show)\
joker/update sets joker for stock in market

### API Requests

<table>
  <tr>
   <td> HTTP </td> <td> URL </td> <td> Body </td>
  </tr>

  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/market/{leagueid} </td>
  <td>

`-`

  </td>
  </tr>

  <tr>
  <td> Put </td>
  <td> http://localhost:8080/api/v1/market/joker/update/{leagueid} </td>
  <td>

```json
{
  "userid": "15787aab-9ec8-1111-1111-testuserb16a",
  "symbol": "ABEA.DE"
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


___

## StocksByUser

API Requests and responses for the stocksByUser controller\
Posts to buy and sell stock
Get-request to get amount of stock owned. This is needed to determine how much stock a user can sell.


### API Requests

<table>
  <tr>
   <td> HTTP </td> <td> URL </td> <td> Body </td>
  </tr>

  <tr>
  <td> Post </td>
  <td> http://localhost:8080/api/v1/stockByUser/BuyStock </td>
  <td>

```json
{
"userid": "15787aab-9ec8-1111-1111-testuserb16a",
"leagueid": "1d701622-4e4c-1111-1111-testleague69",
"symbol": "ABEA.DE",
"bidPrice": "1",
"amount": "3"
}
```
  </td>
  </tr>

  <tr>
  <td> Post </td>
  <td> http://localhost:8080/api/v1/stockByUser/SellStock </td>
  <td>

```json
{
  "userid": "15787aab-9ec8-1111-1111-testuserb16a",
  "leagueid": "1d701622-4e4c-1111-1111-testleague69",
  "symbol": "ABEA.DE",
  "bidPrice": "1",
  "amount": "3"
}
```

  </td>
  </tr>

<tr>
  <td> Get </td>
  <td> http://localhost:8080/api/v1/stockByUser/GetStockAmountOwned </td>
  <td>

```json
{
  "leagueId": "XL8L3M_SUID",
  "userId": "15787aab-9ec8-1111-1111-testuserb16a",
  "symbol": "YOC.DE"
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

___

## UserByLeagues

API Requests and responses for the userByLeagues controller\
get current depotvalue\
get full depot request


### API Requests

<table>
  <tr>
   <td> HTTP </td> <td> URL </td> <td> Body </td>
  </tr>

  <tr>
  <td> Get </td>
  <td> http://localhost:8080/api/v1/valueDepot </td>
  <td>

```json
{
  "leagueid": "1d701622-4e4c-1111-1111-testleague69",
  "userid": "15787aab-9ec8-1111-1111-testuserb16a"
}
```
  </td>
  </tr>

<tr>
  <td> Get </td>
  <td> http://localhost:8080/api/v1/freeBudget </td>
  <td>

```json
{
  "leagueid": "1d701622-4e4c-1111-1111-testleague69",
  "userid": "15787aab-9ec8-1111-1111-testuserb16a"
}
```
  </td>
  </tr>

  <tr>
  <td> Get </td>
  <td> http://localhost:8080/api/v1/Depot </td>
  <td>

```json
{
  "leagueid": "1d701622-4e4c-1111-1111-testleague69",
  "userid": "15787aab-9ec8-1111-1111-testuserb16a"
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


## Transactions

API Requests and responses for the user controller.

_Always look at the message in the JSON response for further information if you get an error._

With this Get request you get all transactions within a league. Maybe for future work we need to limit the Get-response to x transactions (maybe x = 100).
### API Requests

<table>
  <tr>
   <td> HTTP </td> <td> URL </td> <td> Body </td>
  </tr>

 

  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/transactions/league/all/{leagueId} </td>
  <td>

`-`

  </td>
  </tr>

  
</table>

