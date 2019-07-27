# Assignment-Queuing-System
A. Problem specification 

Your company, a regional software developer with 455 staffs, has received 3 new projects this month: two on-line games, and a queuing system for a local hospital. All three of the projects are equally important, with certain level of technical difficulties and challenges. You have been instructed by the CEO to lead a team of 4-5 members, to handle one of the project. You have been given 6 weeks to complete the project and prepare a demo for the board of directors. The descriptions of the three projects are enclosed as appendices. You will be assigned one of these projects once you have formed the team. 

B. Group formation 
1. Form a group of 4-5 members to do the project. 
2. Every group member must contribute to the project, including certain amount of coding. The role played by and contribution of each member to the project must be included in the managerial report. 

C. Submission 
1. A technical report explaining the assigned task, the requirements of the task, the approach taken to solve the task, a detail description of your solution (includes the flow-chart, modules, etc.), sample snapshot of your program output. 
2. A managerial report explaining the formation of the group, role and assigned work for each of the members, the project timeline, the problems faced in accomplishing this assignment and your solutions, and other issues arise. 
3. The complete source code. 

D. Important dates: 
1. Submission: (week 13th) 12:00pm on Friday, 15th December 2017 
2. Demo: (week 14th) during lab

Appendix

A. The requirements of the queueing system: 
  
   A queue number dispenser:
  1. There is a queue number dispenser machine with 3 service category
  buttons on it:
  
       Category ‘A’ button - General Enquiry
   
       Category ‘B’ button - Technical Assistance
   
       Category ‘C’ button - Billing/Payment
      
  2. Each service category should use a different range of number.
  3. A function to dispense the queue number.


   Service counter:
  1. There are only 4 service counters available on the customer service
  department, where each counter serves different category of the services:
       General Enquiry - Counter 1 and 2
       Technical Assistance - Counter 3
       Billing/Payment - Counter 4
  2. The service counters are able to perform 2 functions:
       Next queue number - Call the next queue number
       Repeat queue number - Repeat the latest queue number for a maximum of 3 times


   A display screen:
  1. There are two columns of data that should be display on the display
   screen which are a 3 digits queue number and a single digit number of the
  service counter.
   2. The display screen should only display 4 rows of queue number with its
  respective service counter number.
  3. The latest request of the queue number should be display at lowest row of
  the display screen.
  4. If a queue number that are appearing on the display screen but not in the
  last row placement is being repeat, the display screen should move the
  repeat queue number to the last row without remove any existing queue
  number on the display screen.


   Reporter
  1. Generate a summary report of the queueing system.


Your team has to write a program to simulate the queuing system, i.e. showing all of the
functionalities describe above.


B. Constrains:
However, due to budget and space constraint, the hardware machine purchased for the
queueing system has a relatively low memory. The machine is only able to hold 40
queue number at a time including the queue number that are on the display screen. 


C. Extra features:

 GUI

 Audio, i.e. play sound when a queue number is being called or repeat

 Logging, i.e. log every event occurs such as when a number is been called, etc.
