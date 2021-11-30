# Overview

The third and final iteration is the most complex yet, requiring the development of a graphical user interface (or interfaces) and all the use cases involving it.  Your team is now big and simply organizing and managing it will become a task in itself.

## Team

You will be assigned to a 10-15-person team.

You are **required** to contact your teammates to help organize the project.

You are **required** to participate in and contribute to your team's project.

Failure to do these things or failure to do them in a timely manner will result in reduction of **your** grade, regardless of the overall performance of the team.

In addition, be aware that organizing and managing the work of this many people is not straightforward.

## Requirements

### _Team-based aspects_

You and your team **must** develop a portion of the control software for the _SelfCheckoutSystem_, atop the hardware simulation provided to you in the attached ZIP file as _SelfCheckoutSystem_, v3.0. You **must not** alter the source code therein.

You and your team **must** extend the control software for the _SelfCheckoutSystem_ to support the following use cases:

- Customer returns to adding items
- Customer does not want to bag a scanned item
- Customer looks up product
- Customer enters PLU code for a product
- Customer enters their membership card information
- Customer pays with gift card
- Customer removes purchased items from bagging area
- Customer enters number of plastic bags used
- Station detects that the weight in the bagging area does not conform to expectations
- Station detects that the paper in a receipt printer is low.
- Station detects that the ink in a receipt printer is low.
- Attendant approves a weight discrepancy
- Attendant removes product from purchases
- Attendant looks up a product
- Attendant adds paper to receipt printer
- Attendant adds ink to receipt printer
- Attendant blocks a station
- Attendant empties the coin storage unit
- Attendant empties the banknote storage unit
- Attendant refills the coin dispenser
- Attendant refills the banknote dispenser
- Attendant logs in to their control console
- Attendant logs out from their control console
- Attendant starts up a station
- Attendant shuts down a station

You may extend any of the implementations that your teammates worked on in the second iteration; it is for the team to decide which one is the best option to proceed with.

You and your team **must** develop an **automated** test suite for testing your application.  Logical test case selection and coverage both matter.

You and your team **must** provide structural, sequence/communication, and state diagrams (multiple diagrams are likely necessary) to explain how your application works.

You and your team **may** provide a supplementary, one (1) page explanation of how your application works.  This should aim to help the reader interpret the diagrams.

You and your team **must** provide a Git log that demonstrates who performed commits over time. (This can be used partially as evidence regarding individual opinions; see below.)

### _Organization_

There are multiple aspects of the work to be considered here:

- design;
- implementation;
- automated testing, bug reports, bug repairs;
- documenting;
- managing and tracking;
- presenting.

All of these aspects will require efforts and are equally important.  The same person can perform multiple roles, or roles can switch as necessary. I expect different people to be more interested in some of these than in others. It is your collective job to figure out how to organize.  All information about all these tasks should be recorded in files and committed to the group's Git repository, which

It is PARAMOUNT that you and your team decide on who will do which of these tasks (or more specific ones, like implementing a certain use case).  To do that, you need to detail immediately what all those tasks are, and then be prepared to change that list when you understand the details better.

The job of the **design/implementation team(s)** should be familiar to you now.

The job of the **management team** will be to maintain/update the list of tasks, the assignment to individuals (done by consensus or by someone being the boss), and the completion status.  It is important to have a fallback position if your team doesn't manage to complete everything by the deadline.  It is also important that we and you all know who was supposed to do what and who actually did (or didn't do) what.

The job of the **quality assurance team** will be to write automated test cases, run them, write bug reports, and assign them to people who are most appropriate to repair them.

The **documentation team** needs to create the required models.  There are potentially a lot of these.  What details are key?  Which can be abstracted away?  Try to explain the reality with its strengths and weaknesses.  It is unrealistic to expect that the design you came up with is fantastic: aim for "good enough" and recognize what is good and bad about it.

The work of the **presentation team** will be: (1) to demonstrate that ALL the use cases for the system are supported (or that they were not completed; (2) to explain the design of your software; and (3) to demonstrate the automated test suite, in terms of number of tests and number of passing tests and in terms of line coverage.  The presentation will be due a week after the end of classes, and will be used for assessing Iteration 3 grades in part and for assessing the final examination in part.  You will have 15 minutes maximum for each of these three parts (i.e., 45 minutes in total). More details will be posted separately.

### _Peer/self evaluations_

Individual peer/self evaluations must be submitted via the relevant [survey instrument](/d2l/common/dialogs/quickLink/quickLink.d2l?ou=354623&type=survey&rCode=UCalgary-1926827). The instructions are given there.

## Advice

When deadlines loom, you are most likely to make a dumb mistake.

Make sure that you read your TA's comments from the second iteration and correct your mistakes!

Remember: In a good team, everyone contributes, but not necessarily in the same way.

## Solution Submission

You are required to submit:

1. your diagrams (plus a cover page with the names and student numbers of your teammates, plus an optional explanation page) as a single **PDF document**;
2. a ZIP file containing your exported Eclipse project `SelfCheckoutSystem - Software` that contains your extended control software;
3. a ZIP file containing your exported Eclipse project `SelfCheckoutSystem - Software - Test` that contains your automated test suite; and,
4. your Git log file

in this Dropbox folder before the due date/time.  Your Dropbox comments can explain sources of information you have used, as described below.

## Late Penalties

Submissions made more than 48 hours beyond the due date/time will receive an F.

The late penalty will otherwise be calculated as:

`late_penalty = min { hours_late, 48 } / 48`

`hours_late` will be determined according to D2L. Submissions that are less than an hour late will receive no late penalty (i.e., there is a grace period of an hour).

## Collaboration and Plagiarism

This is a group assignment as explained under the course [Collaboration and Plagiarism Policy](/d2l/common/dialogs/quickLink/quickLink.d2l?ou=354623&type=content&rcode=UCalgary-768812).  It is to be performed strictly within your assigned group.  Questions may be asked on the D2L Discussion Forum.  Assistance in practical matters (setting up an Eclipse project, running the debugger, etc.) are acceptable from students outside your group, but discussion of details of the assignment solution **cross the line**.  If in doubt, ask us first!

You must cite all sources of information that you use in your solution (other than materials that we directly hand you, like this description, the course notes, details posted to the Discussion Forum).  You can list these in the comments when you submit your solution.
