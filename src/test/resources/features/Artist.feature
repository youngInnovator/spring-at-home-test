Feature: Verify Artist Operations

  @ArtistOperations
  Scenario Outline: Delete artist object
    Given Artist exist in system with id 1
    When I send request to delete artist with id "<id>"
    Then the response will return status <statusCode>
    And Artist table contain <rows> rows
    Examples:
      | id  | statusCode | rows |
      | 1   | 200        | 0    |
      | 200 | 404        | 1    |

  Scenario Outline: Send a valid Request to create artist object
    When I send a request to create valid artist with firstName "<firstName>", middleName "<middleName>" lastName "<lastName>", category "<category>", birthday "<birthday>", email "<email>" and notes "<notes>"
    Then the response will return status 201 and firstName "<firstName>", middleName "<middleName>", lastName "<lastName>", category "<category>", birthday "<birthday>", email "<email>" and notes "<notes>"
    And Artist table contain 1 rows

    Examples:
      | firstName | middleName | lastName | category | birthday   | email         | notes      |
      | testFirst | testMiddle | testLast | ACTOR    | 2021-12-09 | test@test.com | test notes |
      | testFirst |            | lastTest | ACTOR    | 2021-12-09 | test@test.com |            |

  Scenario Outline: Send a inValid Request to create artist object
    When I send a request to create inValid artist with firstName "<firstName>", middleName "<middleName>" lastName "<lastName>", category "<category>", birthday "<birthday>", email "<email>" and notes "<notes>"
    Then the response will return status 400 and error contains "<errorMessage>"
    And Artist table contain 0 rows

    Examples:
      | firstName | middleName | lastName | category | birthday   | email         | notes      | errorMessage                         |
      |           | testMiddle | testLast | ACTOR    | 2021-12-09 | test@test.com | test notes | firstName cannot be empty.           |
      | testFirst | testMiddle |          | ACTOR    | 2021-12-09 | test@test.com | test notes | lastName cannot be empty.            |
      | testFirst | testMiddle | testLast | ACTO5    | 2021-12-09 | test@test.com | test notes | Category value ACTO5 is incorrect.   |
      | testFirst | testMiddle | testLast | ACTOR    | 2021-18-09 | test@test.com | test notes | Invalid Date value: 2021-18-09       |
      | testFirst | testMiddle | testLast | ACTOR    | 2021-12-09 | testtest.com  | test notes | email must be a valid email address. |

  Scenario Outline: Find artist using multiple criterion
    Given Artist exist in system with firstName "yarti" lastName "grad" category "ACTOR" dateOfBirth "2021-12-10"
    Given Artist exist in system with firstName "testr" lastName "grad" category "ACTOR" dateOfBirth "2021-11-10"
    Given Artist exist in system with firstName "resty" lastName "yart" category "PAINTER" dateOfBirth "2021-11-10"
    When I send request to find artist with category "<category>" birth-month "<birthMonth>" search "<search>"
    Then the response will return status <statusCode>
    And result will contain <numberOfRecords> enteries
    Examples:
      | category | birthMonth | search | statusCode | numberOfRecords |
      | ACTOR    |            |        | 200        | 2               |
      |          | 12         |        | 200        | 1               |
      |          |            | rest   | 200        | 1               |
      |          |            | yar    | 200        | 2               |
      |          | 12         | yar    | 200        | 1               |
      | ACTOR    | 11         |        | 200        | 1               |
      | ACTOR    |            | yar    | 200        | 1               |
      | ACTOR    | 12         | yar    | 200        | 1               |