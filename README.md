# programming-linguistics

4학년 1학기 프로그래밍 언어론 과제

![image](https://user-images.githubusercontent.com/63775931/94992195-3b80f200-05c3-11eb-8ea5-ff0aab963914.png)
실행할 test.txt

![image](https://user-images.githubusercontent.com/63775931/94992197-3de34c00-05c3-11eb-83a8-5a1ab1c36b74.png)
실행 결과

-------------------------------------------------------

![image](https://user-images.githubusercontent.com/63775931/94992223-72ef9e80-05c3-11eb-9d46-1d1daf1d31fb.png)

![image](https://user-images.githubusercontent.com/63775931/94992224-7551f880-05c3-11eb-9679-a4cf16780844.png)
메인 함수를 찾고 block의 member마다 M(statement, sigma)을 실행

---------------------------------------------------

![image](https://user-images.githubusercontent.com/63775931/94992234-8569d800-05c3-11eb-8300-d45a5694bea5.png)

![image](https://user-images.githubusercontent.com/63775931/94992237-88fd5f00-05c3-11eb-9bfa-b6fa805983d5.png)
CallStatement 발견 시에 addFrame 실행

![image](https://user-images.githubusercontent.com/63775931/94992238-8ac72280-05c3-11eb-805b-5e6401dfba9e.png)
해당하는 f를 찾고 이에 대해 재귀적으로 M(body, sigma)를 실행

-----------------------------------------


![image](https://user-images.githubusercontent.com/63775931/94992254-a8948780-05c3-11eb-9473-f9f1ee80e38a.png)

![image](https://user-images.githubusercontent.com/63775931/94992256-aa5e4b00-05c3-11eb-891f-e8fb0e10fb19.png)
Source에 해당하는 새로운 상태를 만들어서 sigma와 onion

![image](https://user-images.githubusercontent.com/63775931/94992257-ac280e80-05c3-11eb-8e76-f14a136c0ffd.png)
sigma에서 변수에 해당되는 값을 mu에서 찾아서 return

