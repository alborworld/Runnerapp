curl \
-v \
-H "Content-Type: application/xml" \
-X POST \
-d '<runnerUpdate name="James" country="Australia" km="4" xmlns="http://com.alborworld/schema/Runner"/>' \
http://localhost:9090/runnerapp/sendRunnerStatusUpdate
