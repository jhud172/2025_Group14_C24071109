# Calendar Day JMeter Test Plan

Base URL: `http://localhost:8080`

## Flow

1. `GET /login`
2. Extract CSRF token from the hidden input if it is present.
3. `POST /login`
   - `username=demo`
   - `password=Demo123!`
   - include the CSRF parameter if present
4. `GET /calendar/day/2026-01-20`

## Components

- HTTP Request Defaults
- HTTP Cookie Manager
- HTTP Header Manager if needed
- CSS Selector Extractor or Regular Expression Extractor for CSRF
- Thread Group
- Aggregate Report
- Summary Report
- View Results Tree for debugging only

## Load Levels

- 1 user
- 5 users
- 10 users
- 25 users

## Metrics To Record

- Average response time
- Median response time
- 95th percentile
- Throughput
- Error percentage
- Slowest request
- Notes from application logs
- Notes from IntelliJ Profiler

## Result Notes

Fill this in after running JMeter. Do not add estimated or invented measurements.
