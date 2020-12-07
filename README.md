# Smart Gyms Schedule Manager

>This is a Spring Boot Web Application to manage and schedule appointments between gym trainers and members. It has many features such as automatic invoicing, email notifications, appointment cancellation, trainer working plans etc.

This project is based on the work of Slabiak: https://github.com/slabiak/AppointmentScheduler.git


What sets this project apart is its ability to:
    
    -provide a live metric of the gym capacity
    
    -support multiple franchise locations
    
    -allow trainers to adjust their time availabilty
    
    -a refined U.I.
  
<img width="1116" alt="Screen Shot 2020-12-07 at 1 13 48 PM" src="https://user-images.githubusercontent.com/60204834/101388518-155f2300-388e-11eb-9b2d-1af145a03d60.png">

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

`admin` -  Admin can add new trainers, classes and assign classes to trainers. Admin can see list of all: appointments, trainers, members, invoices. He can also issue invoices manually for all confirmed appointments.

`trainer` - can be created by admin only. Trainers can set their own working plan, add breaks to that working plan and change the classes they teach.

`member regular` - registration page is public and can be visited by everyone. Member can book new appointments and manage them. This type of member sees only services which target regular member.

`member premium` - almost the same as a regular member. The only difference is that this type of account can see services which target premium members.

## Booking process

To book a new appointment member needs to click `New Appointment` button on all appointments page and then:

1. Choose desired class from available classes list
2. Choose trainer for the selected class
3. Choose one of the available dates and times
4. Click book on confirmation page

Live Gym Capacity is refreshed each time home page is loaded:

This is done by retrieving the start and end time for every scheduled class and checking to see if this interval contains the current time in EST. If it does, then the initial max capacity is decremented by 1. The initial value can be specified by the gym owner. In the demo, the initial max capacity is set to 30. 

Available hours are calculated with getAvailableHours function from AppointmentService:

`List<TimePeroid> getAvailableHours(int providerId,int customerId, int workId, LocalDate date)`

This function works as follow:
1. gets selected trainer working plan
2. gets working hours from working plan for selected day 
3. excludes all breaks from working hours
4. excludes all trainer-booked appointments for that day
5. excludes all member-booked appointments for that day
6. gets selected work duration and calculate available time periods 
7. returns available hours

## Appointments lifecycle
**1. Every appointment has it's own status. Below you can find description for every possible status:**

| Status | Set by | When | Condition |
| --- | --- | --- | -- |
| `scheduled` |system | New appointment is created | -|
| `finished` | system | Current date is after appointment end time  | current appointment status is `scheduled` and current date is after appointment end time|
| `confirmed` | system | Current date is 24h after appointment end time  |current appointment status is `finished` and current date is more than 24h after appointment end time|
| `invoiced` | system |Invoice for appointment is created | -|
| `canceled` | member |Member clicks cancel button |current appointment status is `scheduled` and current date is not less than 24h before appointment start time and user total canceled appointments number for current month is not greater than 1|
| `rejection requested` | member |Member clicks reject button |current appointment status is `finished` and current date is not more than 24h after appointment end time|
| `rejection accepted` | provider |Trainer clicks accept rejection button | current appointment status is `rejection requested`|

**2. Normal appointment lifecycle is:**

1. scheduled - after user creates new appointment
2. finished - after system time is after appointment end time
3. confirmed - after system time is more than 24h after appointment end time and user didn't request rejection
4. invoiced - after invoiced is issued automatically on the 1st day of next month


**3. Apppointment cancellation**

Every appointment can be canceled by member or trainer. Member is allowed to cancel 1 appointment in a month no less than 24h before appointment start date. Trainer is allowed to cancel his appointments without any limit as long as the appointment status is `scheduled`. 

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


