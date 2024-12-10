cp /home/chaeryeon0402/application.yml /var/lib/jenkins/workspace/backend-pipeline/backend/src/main/resources/application.yml

sudo nohup java -jar /var/lib/jenkins/workspace/backend-pipeline/backend/build/libs/backend-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

echo "deploy 파일 실행했다"