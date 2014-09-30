curl \
-v \
-H "Accept: application/xml" \
-G \
-d "criteria=SORT_BY_DISTANCE" \
-d "order=DESCENDING" \
http://localhost:9090/runnerapp/getCountryList
