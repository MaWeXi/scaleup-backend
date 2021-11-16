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
  </tr>
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

  `No content`

  </td>
  </tr>
  <tr>
  <td> GET </td>
  <td> http://localhost:8080/api/v1/user/all/{id} </td>
  <td>

  `No content`

  </td>
  </tr>
  </tr>
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

  ```json
  No content
  ```

  </td>
  </tr>
  </tr>
  <td> 400 </td>
  <td>

  **Markdown** _here_. (Blank lines needed before and after!)

  </td>
  </tr>
</table>

___

## Leagues
