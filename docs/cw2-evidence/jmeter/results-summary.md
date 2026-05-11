# Calendar Day JMeter Results Summary

Metrics below are computed from the latest successful sample block in each CSV file. Earlier failed connection-refused attempts from app restart windows were excluded from the comparison.

## BEFORE Results

| Users | Samples | Average ms | Median ms | 95th Percentile ms | Throughput | Error % |
| ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 1 | 30 | 81.47 | 63.00 | 327.00 | 18.43/s | 0.00 |
| 5 | 150 | 45.94 | 40.50 | 106.00 | 31.36/s | 0.00 |
| 10 | 300 | 38.33 | 26.50 | 95.00 | 58.33/s | 0.00 |
| 25 | 750 | 37.81 | 24.50 | 92.00 | 139.15/s | 0.00 |

## AFTER Results

| Users | Samples | Average ms | Median ms | 95th Percentile ms | Throughput | Error % |
| ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 1 | 30 | 83.77 | 66.50 | 357.00 | 17.77/s | 0.00 |
| 5 | 150 | 46.47 | 34.00 | 108.00 | 31.38/s | 0.00 |
| 10 | 300 | 41.26 | 30.00 | 99.00 | 57.53/s | 0.00 |
| 25 | 750 | 39.69 | 30.00 | 94.00 | 137.87/s | 0.00 |

## Before/After Comparison

Positive percentage change means improvement. Negative means the AFTER run was slower.

| Users | Before Average ms | After Average ms | Average Change | Before 95th ms | After 95th ms | 95th Change | Notes |
| ---: | ---: | ---: | ---: | ---: | ---: | ---: | --- |
| 1 | 81.47 | 83.77 | -2.82% | 327.00 | 357.00 | -9.17% | Slightly slower after the index restore. |
| 5 | 45.94 | 46.47 | -1.15% | 106.00 | 108.00 | -1.89% | Difference is very small and likely noise. |
| 10 | 38.33 | 41.26 | -7.64% | 95.00 | 99.00 | -4.21% | AFTER was slower on this local run. |
| 25 | 37.81 | 39.69 | -4.97% | 92.00 | 94.00 | -2.17% | AFTER was slightly slower overall. |

## Limitations

- The local H2 dataset is small, so the optimiser may keep using the existing `user_id` path instead of clearly switching to the composite index.
- JVM warm-up and local machine variability are large compared with the small timing differences here.
- Authentication/session handling and Thymeleaf rendering add overhead that is not isolated by the composite index alone.
- The raw CSVs include earlier failed attempts from app restart windows, so the final comparison uses the latest successful sample block in each file.
- The 1-user run is especially noisy because it includes login and redirect work, not just the calendar query.
