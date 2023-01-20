# 개인프로젝트 1.13 ~ ..

## 목표
- REST API 방식으로 프론트부터 서버까지 개발하기
- JPA 적용
- AWS EC2를 이용해 배포까지 진행해보기

## 사용기술

Java, SpringBoot, SpringDataJpa, SpringSecurity, MySql, AWS(EC2, RDS)

## 주요기능

- 회원 CRUD
- 유저 별 게시물 필터링(좋아요 여부, 댓글 여부)
- 게시판 CRUD
- 댓글 및 대댓글 기능
- 좋아요 기능
- 게시물 리스트 필터링 (작성순, 좋아요순)

## ERD


## 현재 구현 상태
**Commits on Jan 13, 2023**

- [x]  프로젝스 생성 및 구글 소셜 로그인 REST API 구현하기

**Commits on Jan 14, 2023**

- [x]  구현해야할 기능들 README 작성
- [x]  구글 access 토큰 가져오기 성공

**Commits on Jan 15, 2023**

- [x]  UserCRUD 구현
- [x]  UserLogin 기능 구현
- [x]  JWT 토큰 인증,인가 구현
- [x]  UserDTO 와 CustomException 생성

**Commits on Jan 16, 2023**

- [x]  (Board, Like, Reply)Entity 생성
- [x]  (Board, Like, Reply)dto 생성
- [x]  Board CRUD 구현
- [x]  Reply CRUD 구현
- [x]  대댓글 기능 구현

**Commits on Jan 17, 2023**

- [x]  게시물 좋아요 기능 구현
- [x]  내가 좋아요 누른 게시물 출력 기능 구현
- [x]  내가 댓글단 게시물 조회 기능 구현
- [x]  내가 작성한 게시물 조회 기능 구현

**Commits on Jan 18, 2023
- [x]  EC2 배포 RDS 설정 완료

**Commits on Jan 19, 2023
- [x]  프론트 프로젝트 생성
- [x]  Header Footer 스켈레톤 생성 
- [x]  게시판 초기 화면 생성

**Commits on Jan 19, 2023
- [x]  로그인 초기 화면 생성
