| Worker | Tests | Total Duration (ms) |
|--------|-------|----------------------|
| ForkJoinPool-1-worker-6 | ui.[2] amount=4999.99 (5411 ms) | 5411 |
| ForkJoinPool-1-worker-1 | ui.[3] amount=5000.00 (5411 ms) | 5411 |
| ForkJoinPool-1-worker-4 | ui.[1] amount=0.01 (5426 ms) | 5426 |
| ForkJoinPool-1-worker-5 | ui.[1] amount=000000001, expectedBalance=0.00, message=Please enter a valid amount. (3587 ms) | 3587 |
| ForkJoinPool-1-worker-2 | ui.[2] amount=5001, expectedBalance=0.00, message=❌ Please deposit less or equal to 5000$. (2743 ms) | 2743 |
| ForkJoinPool-1-worker-3 | ui.userDoesNotFillRequiredFieldsTest() (5630 ms) | 5630 |
