cp /home/chaeryeon0402/application.yml /var/lib/jenkins/workspace/backend-pipeline/backend/src/main/resources/application.yml

cd /var/lib/jenkins/workspace/backend-pipeline/backend

l

./gradlew clean build

# 빌드 성공 여부 확인
if [ $? -ne 0 ]; then
  echo "Gradle 빌드 실패"
  exit 1
fi

# 실행 중인 애플리케이션 종료 (기존 프로세스 종료)
PID=$(ps -ef | grep '[j]ava -jar' | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
  echo "기존 애플리케이션 종료 중 (PID: $PID)"
  sudo kill -9 $PID
else
  echo "!!!!!!!!!!!!!!!!No running server found!!!!!!!!!!!!!!!!!!"
fi

/var/lib/jenkins/workspace/backend-pipeline/backend

sudo nohup java -jar /var/lib/jenkins/workspace/backend-pipeline/backend/build/libs/backend-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &

echo "deploy 파일 실행했다"