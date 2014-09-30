curl \
-v \
-H "Content-Type: application/xml" \
-X POST \
-d '<runnerUpdate name="James" country="Australia" km="4" xmlns="http://com.alborworld/schema/Runner"/>' \
http://localhost:9090/runnerapp/sendRunnerStatusUpdate

curl \
-v \
-H "Content-Type: application/xml" \
-X POST \
-d '<runnerUpdate name="Jose" country="Spain" km="25" xmlns="http://com.alborworld/schema/Runner"/>' \
http://localhost:9090/runnerapp/sendRunnerStatusUpdate

curl \
-v \
-H "Content-Type: application/xml" \
-X POST \
-d '<runnerUpdate name="Wouter" country="The Netherlands" km="10" xmlns="http://com.alborworld/schema/Runner"/>' \
http://localhost:9090/runnerapp/sendRunnerStatusUpdate

curl \
-v \
-H "Content-Type: application/xml" \
-X POST \
-d '<runnerUpdate name="Matteo" country="Italy" km="15" xmlns="http://com.alborworld/schema/Runner"/>' \
http://localhost:9090/runnerapp/sendRunnerStatusUpdate

curl \
-v \
-H "Content-Type: application/xml" \
-X POST \
-d '<runnerUpdate name="Jost" country="The Netherlands" km="20" xmlns="http://com.alborworld/schema/Runner"/>' \
http://localhost:9090/runnerapp/sendRunnerStatusUpdate
