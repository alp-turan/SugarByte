# Diabetes monitoring mobile application - SugarByte 
## Project overview
This project aims to provide an accesible and comprehensive mobile application (app) for diabetic users to log their blood glucose, carbs eaten, and other factors affecting their overall health. 

Our app, SugarByte, differentiates itself from existing diabetic logbook applications through its implementation of an alert system to notify doctors of at-risk glucose readins, as well as through the app's structured logbook approach. Namely, our simple, comprehensive, and intensive logs allow us to cater to diabetic users at varying risk-levels. 

The database of users and log entries was developed using SQL, and all of the UI and functions were developed using java on IntelliJ IDEA.

## Features of SugarByte
- 3 different logbook types: 
1. Simple: only Blood Glucose and carbs eaten at different times of the day.
2. Comprehensive: Blood glucose, carbs eaten and medication dose (insulin or other) at different times of the day.
3. Intensive: Blood glucose, carbs eaten, type ad dose of medication (insulin or other), food diary, duration and type of exercise, and any other unusual events.
   
- Calendar page to view all logged entries.
  
- Alert system that sends an email to the user's doctor if glucose levels recorded are outside of the healthy range.
    - NOTE 1: As set by the WHO, at-risk blood glucose readings are:
       - Hypoglycaemia: 3.9mmol/l as the minimum healthy threshold at any point in the day
       - Hyperglycemia: 11.0 mmol/l as the max healthy threshold if >2 hours have passed since the last meal, and 7.0mmol/l when fasting (we classified fasting as 10 hours, since most medical literature considers fasting to be between 8-12 hours since eating).
   - NOTE 2: the alarm system is built around the assumption that all 'Post-' meal glucose readings are taken immediately after eating, or at the very most within 2 hours of the meal. Hence, the alert system was build based on the 'Pre-' meal glucose values only. 
 
## Code structure:
- 1. Database package: handles all of the database-related aspects, such as saving log entries and users correctly.
     - Class: DatabaseManager
     - Class: LogEntryDAO
     - Class: UserEntryDAO
- 2. Model package: the blueprint for all users ('User' objects) and log entries (LogEntry objectes - whether simple, comprehensive, or intensive).
     - Class: LogEntry
     - Class: User
- 3. Service package: contains the code for the alert system in AlarmService (to notify the user's doctor), and for checking whether log entries need to have the alert system triggered in LogService
     - Class: AlarmService
     - Class: LogService
- 4. UI package: encompasses all of the frontend and UI aspects of the app for all features/pages/windows.
     - Class: BaseUI (which most other classes in UI inherit from)
     - Class: Calendar
     - Class: ComprehensiveLogbook
     - Class: CreateAccount
     - Class: GlucoseGraph
     - Class: GlucoseIndicator
     - Class: Home
     - Class: IntensiveLogbook
     - Class: Logbook
     - Class: Login
     - Class: OpeningWindow
     - Class: Profile
- 5. Test package: comprises of all the unit testing code 
     - databaseTest package: the unit testing code for the database classes
        - Class: DatabaseManagerTest
        - Class: LogEntryDAOTest
        - Class: UserDAOTest
     - serviceTest package: the unit testing code for the service classes
        - Class: AlarmServiceTest
        - Class: LogServiceTest
          
There are in total 22 references throughout our code. The references are structured in the same order as the code (ie reference 1 is in the Database Manager class, and the references are added chronologically starting from the DatabaseManager class until the final 22nd reference which is in the LogServiceTest class). 
      

## Contributing to the development
In order to contribute to our project, please set up your environment as follows:

### Pre-requisites
   - JDK corretto 16 or higher is required for this project. If you already have the IntelliJ IDEA installed, you can select or download the desired JDK from the IntelliJ interface during the setup of the project. Otherwise, install JDK corretto 16 or higher before actually opening/ setting up the project. 
   - If using another IDE which is not IntelliJ, ensure you have set Gradle up manually or with the help of extensions. However, the IntelliJ IDEA is recommended as the steps outlined below for setting up the project locally pertain to the IntelliJ IDEA specifically. 

### Local development
- Open the Project in IntelliJ IDEA.
- Launch IntelliJ IDEA and select Open from the Welcome screen.
- Navigate to the cloned repository folder and open it.
- Import Gradle Project. IntelliJ should automatically detect the Gradle project and prompt you to import it. If not, go to File > Project Structure and manually configure the Gradle settings.
- Go to File > Project Structure > Project and ensure the Project SDK is set to JDK Corretto 16 or higher.
- Click on the Gradle tab (usually on the right side of the IDE) and select the Tasks > build > build option, to correctly build the project locally. 
- To run the application, locate the Main class, right-click on it, and select Run.

## Contributors:
- [Lucia van den Boogart Castro](https://github.com/luciavdbc)
- [Alp turan](https://github.com/alp-turan)
- [Victoria Walker](https://github.com/viickywalker) 
- [Mark Arditi](https://github.com/mark-arditi)
- [Ethan Chang](https://github.com/johnyeocx)



  




