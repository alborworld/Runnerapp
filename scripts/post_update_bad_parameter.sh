curl \
-v \
-H "Content-Type: application/xml" \
-X POST \
-d '<runnerUpdate name="Jack" country="Australia" km="-14" xmlns="http://com.alborworld/schema/Runner"/>' \
http://localhost:9090/runnerapp/sendRunnerStatusUpdate
