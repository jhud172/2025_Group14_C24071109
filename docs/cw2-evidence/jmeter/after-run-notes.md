# AFTER Run Notes

- Date/time: 2026-05-11, successful runs started at 13:33:17, 13:33:33, 13:33:45, and 13:34:02 BST.
- JMeter version: 5.6.3.
- App started successfully: yes.
- Login request: success in the final live run.
- Calendar page request: success in the final live run.
- Commands used:
  - `C:\Users\jhuds\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=1 -l docs/cw2-evidence/jmeter/results/after-1-users.csv`
  - `C:\Users\jhuds\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=5 -l docs/cw2-evidence/jmeter/results/after-5-users.csv`
  - `C:\Users\jhuds\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=10 -l docs/cw2-evidence/jmeter/results/after-10-users.csv`
  - `C:\Users\jhuds\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=25 -l docs/cw2-evidence/jmeter/results/after-25-users.csv`
- Errors: one early attempt immediately after the index restore hit connection refused while the app was still restarting; the final AFTER CSVs were captured after the app was live and are the ones used in the summary.
