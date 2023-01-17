목표
* 소셜 로그인 기능을 가진 게시판 만들어보기
* 게시판 기능은 간단한 CRUD,댓글,대댓글,좋아요 기능
* 게시물 필터링 (조회순,좋아요갯수 순)
* 게시물 필터링2 (사용자가 작성한 게시물,사용자가 댓글단 게시물, 사용자가 좋아요한 게시물)
* Spring security 사용해서 인증 인가 처리하기
* 화면 구성은 Vue 사용해서 구성하기(기능위주)
* Swagger 를 통해 API 명세 문서화 해보기
* 최종은 EC2배포 까지 진행해보기



__유저 폼 구현 상태__
- [x] 회원가입 기능 만들기
- [x] 로그인 기능 만들기

- 인증,인가
- [x] AuthenticationPrincipal 사용 
- [x] 로그인시 jwt 토큰 생성후 헤더에 쿠키 생성해서 Response
- [x] 로그아웃시 쿠키 제거하기

__게시판 구현 상태__
- [x] 게시판 CRUD 만들기
- [x] 게시판 댓글 기능 만들기
- [x] 게시판 대댓글 기능 만들기
- [ ] 게시판 좋아요 기능 만들기

__게시글 필터링 구현 상태__
- [x] 아이디 역순으로 출력
- [x] 좋아요 순으로 출력하기

- [ ] 내가 작성한 게시글 모음 출력
- [ ] 내가 댓글작성한 게시글 모음 출력
- [ ] 내가 좋아요 누른 게시글 모음 출력

__소셜 로그인 구현 상태__
- [x] 구글 access token , id token 가져오기 
- [ ] FACEBOOK access token , id token 가져오기
- [ ] NAVER access token , id token 가져오기
- [ ] KAKAO access token , id token 가져오기
- 
- [x] TODO : @CrossOrigin 대신 Spring Security로 설정 변경하기
- [x] UserDomain 만들기
- [x] MySql 연결하기 


__Vue 사용해서 화면 구성하기__


