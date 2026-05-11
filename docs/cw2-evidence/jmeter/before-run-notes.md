# BEFORE Run Notes

- Date/time: 2026-05-11, successful runs started at 13:30:57, 13:31:11, 13:31:26, and 13:31:42 BST.
- JMeter version: 5.6.3.
- App started successfully: yes.
- Login request: success in the final live run.
- Calendar page request: success in the final live run.
- Commands used:
  - `C:\Users\jhuds\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=1 -l docs/cw2-evidence/jmeter/results/before-1-users.csv`
  - `C:\Users\jhuds\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=5 -l docs/cw2-evidence/jmeter/results/before-5-users.csv`
  - `C:\Users\jhuds\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=10 -l docs/cw2-evidence/jmeter/results/before-10-users.csv`
  - `C:\Users\jhuds\Downloads\apache-jmeter-5.6.3\apache-jmeter-5.6.3\bin\jmeter.bat -n -t docs/cw2-evidence/jmeter/calendar-day-load-test.jmx -JTHREADS=25 -l docs/cw2-evidence/jmeter/results/before-25-users.csv`
- Errors: an earlier attempt while the app was restarting returned connection refused; the final BEFORE CSVs were captured after the app was live and are the ones used in the summary.
