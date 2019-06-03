# 시작
* 세션은 서버에 쿠키는 클라이언트에 저장 되는걸로 알고있다.
* 쿠키는 가끔 내 컴퓨터에서 봤었던 기억이 나는데 세션은 서버에 저장한다는걸로 알고 있고 메모리라는 것도 찾긴했다. 근데 눈으로 보질 못했다.
* [현재 알고 있는 사실들](https://gist.github.com/lhy880518/1d5e9c00df0c5c12db67756af85c885c)

## JVM메모리 구조
* 결국 JVM을 통하여 자바 프로그램을 실행 시키고 그 후 할당된 메모리 내의 어떤부분에다가 새로 세션이라는 객체에 담을 데이터를 추가하지 않을까 싶다

![image](https://user-images.githubusercontent.com/24884819/58766961-0ecc5d00-85c0-11e9-9beb-b7b017a88596.png)

![image](https://user-images.githubusercontent.com/24884819/58766957-ffe5aa80-85bf-11e9-842c-898999c15cf7.png)

* 그렇다면 session.setattribute하였을때 객체가 메모리에 할당되지 않을까? 이걸 프로그램 내부에서 확인이 어떻게 가능할까?

## 눈으로 직접보는 Session
* 아래 Reachability의 글에서 확인했듯이 모든 스레드가 공통적으로 공유하게되는 Heap 메모리에 상주하는지에 대한 테스트를 진행한다.
~~~
    @GetMapping("/")
    @ResponseBody
    public String sessionTest(HttpSession session){
        log.info("freeMemory = {}",Runtime.getRuntime().freeMemory());

        session.setMaxInactiveInterval(3);

        for(int i =0 ; i < 100000 ; i++){
            session.setAttribute(String.valueOf(i), i);
        }
        log.info("session.getAttribute(\"0\") = {}",session.getAttribute("0"));

        log.info("freeMemory = {}",Runtime.getRuntime().freeMemory());
      return "session";
    }
~~~
* 위와 같이 Tomcat이 생성 시켜 준 HttpSession객체에 setAttribute함수를 이용하여 메모리를 사용시키고 Runtime객체를 이용하여 메모리를 직접 확인해 본다.
* 호출 시 마다 메모리는 할당되어 졌으며 특정 용량이 남을때까지 계속 사용하다가 Gc가 발생하게 된다.

* 세션이 만료되게 되면 메모리에서 사라질까?
~~~
    @GetMapping("/get")
    @ResponseBody
    public String get(HttpSession session){
        log.info("session.getAttribute(\"0\") = {}",session.getAttribute("0"));
        return "session";
    }
~~~ 
* 위의 setMaxInactiveInterval함수를 이용하여 3초만큼의 유지시간을 만들고 대기 해보았지만 Gc는 발생되지 않았다.
* 허나 /get 통하여 3초뒤에 확인해보니 null값이 리턴되었기에 내부를 들여다 보기로 한다.

![스크린샷 2019-06-03 오후 5 01 29](https://user-images.githubusercontent.com/24884819/58785807-461f2600-8621-11e9-95fd-369a490286ff.png)

* 들여다 봤는데 모르겠다. isValidInternal의 경우 this.isValid를 반환하도록 되어있고 isValid의 경우 isValid() 메소드가 존재 하는데 그걸 사용하는 메소드는 못찾았다.
* 어디 쓸거 같긴한데 어떻게 찾아야 될지 모르것다;; 몇시간을 뚫어져라 찾아봐도 못찾으니 일단 패스해야것다.

## 결론
* 세션은 자바 Heap메모리상에 상주하게 되며 많은 데이터를 쌓을 시 스레드가 늘어남에 따라 그 값이 기하급수적으로 늘어날 것이 분명하므로 사용에 주의를 요한다.
* Session타임 아웃을 정해놓아도 메모리가 해제되는건 아니기에 또한 사용에 주의를 요한다.
  


> [두 그림의 출처](https://limkydev.tistory.com/51)

> [GC의 Reachability](https://d2.naver.com/helloworld/329631)