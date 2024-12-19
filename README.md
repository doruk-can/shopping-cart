# Application Setup

**The application runs with JDK 21.**

## Getting Started

1. **First, run the `docker-compose` command.** This will set up the initial database and create the `Cart` entity for the demo user.

## Database Configuration

- **MongoDB is configured to run on port 27017.** You can interact with the database directly to observe the effects of command-line operations.

## User Cart Association

- Each user in the system is associated with a **single Cart**. For development convenience, I've hardcoded a user ID (`664e161cc98c1c2cdf209c62`) for querying the Cart collection.

## Configuration Tips

- Please note that the `runPostConstruct` configuration under the `app` key in `application.yml` should be set to `true` for post-construct methods to run. You can set it to `false` for Maven clean install operations.

## Output File

- You can find the output file at `tycase/output/results.txt`.


<br>

**Note:** An example entity and an output file is added below.

<img width="568" alt="Pasted Graphic" src="https://github.com/DevelopmentHiring/DorukCan/assets/69071800/087e75ed-044e-468d-b444-0d84edf2da92">

<br><br>


￼
