# Smart Gyms Schedule Manager

>A web application to manage and schedule appointments between gym trainers and members. It offers live monitoring of gym capacity, email notifications, appointment cancellation, trainer working plans and more.

<b>What sets this project apart is its ability to:</b>
    
    -Provide a live metric of the gym capacity
    
    -Support multiple franchise locations
    
    -Allow trainers to adjust their time availabilty
    
    -A refined U.I.
    
<br />
<img width="1316" alt="Screen Shot 2020-12-09 at 11 50 28 AM" src="https://user-images.githubusercontent.com/60204834/101660788-91d03e00-3a15-11eb-936c-fa2822515a1b.png">

<img width="1042" alt="Screen Shot 2020-12-09 at 11 53 30 AM" src="https://user-images.githubusercontent.com/60204834/101660581-55044700-3a15-11eb-9d63-3c70d7b85438.png">


## Demo

The app is deployed and can be found [here](https://smartgymone.herokuapp.com/) 

You can use the following credentials with the demo:

Location A (Default):
| Account type | Username | Password 
| --- | --- | --- |
| `admin` | admin | qwerty123 |
| `trainer` | provider |qwerty123 |
| `trainer` | trainerB |qwerty123 |
| `premium member` | customer_c |qwerty123 |
| `regular member` | customer_r |qwerty123 |

Location B:
| Account type | Username | Password 
| --- | --- | --- |
| `admin` | admin | qwerty123 |
| `trainer` | jdoe |qwerty123 |
| `trainer` | yogamaster |qwerty123 |
| `premium member` | member1 |qwerty123 |
| `regular member` | mem2 |qwerty123 |

Location C:
| Account type | Username | Password 
| --- | --- | --- |
| `admin` | admin | qwerty123 |
| `trainer` | CardioMaster |qwerty123 |
| `trainer` | SwimInstructor |qwerty123 |
| `premium member` | mem2 |qwerty123 |
| `regular member` | mem1 |qwerty123 |

## Account types 

`admin` -  Adds new trainers and classes. Assigns classes to trainers. Sees list of all: appointments, trainers, members, invoices. 

`trainer` -  Creates a working plan, add breaks to that working plan and change the classes they teach.

`member regular` -  Books new appointments and manages them. Sees only classes which target regular members.

`member premium` - Same as regular member except can only see classes which target premium members.

## Booking process

To book a new appointment, members start by clicking the `New Appointment` button on the all appointments page. They must then:

1. Choose a desired class from the list of available classes
2. Choose a trainer for the selected class
3. Choose one of the available dates and times
4. Click book button on the confirmation page

## Notable Functions:

Live Gym Capacity is refreshed each time home page is loaded:

This is done by retrieving the start and end time for every scheduled class and checking to see if this interval contains the current time in EST. If it does, then the initial max capacity is decremented by 1. The initial max capacity can be specified by the gym owner. In the demo, it is set to 30. 

Available hours are calculated with getAvailableHours function from AppointmentService:

`List<TimePeroid> getAvailableHours(int providerId,int customerId, int workId, LocalDate date)`

This function works as follow:
1. Retrieves selected trainer working plan
2. Retrieves working hours from working plan for selected day 
3. Excludes all breaks from working hours
4. Excludes all trainer-booked appointments for that day
5. Excludes all member-booked appointments for that day
6. Retrieves selected work duration and calculates available time periods 
7. Returns available hours

## Appointments lifecycle
**1. Every appointment has it's own status. Below you can find description for every possible status:**

| Status | Set by | When | Condition |
| --- | --- | --- | -- |
| `scheduled` |system | New appointment is created | -|
| `finished` | system | Current date is after appointment end time  | Current appointment status is `scheduled` and current date is after appointment end time|
| `confirmed` | system | Current date is 24h after appointment end time  |Current appointment status is `finished` and current date is more than 24h after appointment end time|
| `invoiced` | system |Invoice for appointment is created | -|
| `canceled` | member |Member clicks cancel button |current appointment status is `scheduled` and current date is not less than 24h before appointment start time and user total canceled appointments number for current month is not greater than 1|
| `rejection requested` | member |Member clicks reject button |current appointment status is `finished` and current date is not more than 24h after appointment end time|
| `rejection accepted` | provider |Trainer clicks accept rejection button | current appointment status is `rejection requested`|

**2. Normal appointment lifecycle is:**

1. scheduled - right after the user creates a new appointment
2. finished - once the system time is later than the appointment end time
3. confirmed - once the system time is more than 24h after appointment end time and the user didn't request rejection
4. invoiced - after invoiced is issued automatically on the 1st day of the next month


**3. Apppointment cancellation**

Appointments can be canceled by a member or trainer (if cancellable option specified by admin). Members are only allowed to cancel one appointment each month, no less than 24h before appointment start date. Trainers are allowed to cancel their appointments without any limit as long as the appointment status is `scheduled`. 

## Notifications
**1. An email notification is sent when:**

+ appointment is finished
+ appointment rejection is requested
+ appointment rejection is accepted
+ new appointment is created
+ appointment is canceled
+ invoice is issued

Email templates can be found here: `src\main\resources\templates\email`


## Built With

* [Fullcalendar](https://fullcalendar.io/) - A JavaScript event calendar
* [FlyingSaucer](https://github.com/flyingsaucerproject/flyingsaucer) - Used to generate invoice PDF
* [jjwt](https://github.com/jwtk/jjwt) - Used to generate/validate JWT tokens

This project is based on the work of Slabiak under the MIT open source license: https://github.com/slabiak/AppointmentScheduler.git
