| Worker | Tests | Total Duration (ms) |
|--------|-------|----------------------|
| ForkJoinPool-1-worker-8 | ui.[1] amount=000000001, expectedBalance=0.00, message=Please enter a valid amount. (1917 ms) | 1917 |
| ForkJoinPool-1-worker-9 | ui.[2] toAcc=null, isConfirm=false, message=❌ Please fill all fields and confirm. (4435 ms) | 4435 |
| ForkJoinPool-1-worker-6 | ui.userDoesNotFillRequiredFieldsTest() (4414 ms) | 4414 |
| ForkJoinPool-1-worker-7 | ui.shouldNotAllowUserToTransferMoreMoneyThanHeHasTest() (5610 ms) | 5610 |
| ForkJoinPool-1-worker-11 | ui.[1] amount=0.01 (3257 ms) | 3257 |
| ForkJoinPool-1-worker-1 | ui.[3] amount=5000.00 (3756 ms) | 3756 |
| ForkJoinPool-1-worker-4 | ui.userTransferToAnotherUserTest() (7597 ms) | 7597 |
| ForkJoinPool-1-worker-5 | ui.[2] amount=5001, expectedBalance=0.00, message=❌ Please deposit less or equal to 5000$. (3400 ms) | 3400 |
| ForkJoinPool-1-worker-2 | ui.[1] toAcc=ACC20000, isConfirm=true, message=❌ No user found with this account number. (4429 ms) | 4429 |
| ForkJoinPool-1-worker-3 | ui.userChangeNameToEmptyTest() (1691 ms) | 1691 |
