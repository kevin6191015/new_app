# ntcu-selab
## front-end
- 安裝 node.js
    - https://nodejs.org/zh-tw/download
    - v20.19.5(LTS)
- 創建前端 Vite + Vue 3
    - `npm create vite@latest front -- --template vue`
    - `npm install`
    - `npm run dev`
- 新專案使用 TS
    - `npm i -D typescript @types/node`
    - `npm i -D vue-tsc @vitejs/plugin-vue`
    - 撰寫 `tsconfig.json`, `env.d.ts`, `vite.config.ts`
        - `tsconfig.json` → 告訴 TypeScript 編譯器（和 VSCode）要怎麼檢查、編譯 .ts/.vue 程式碼。
        - `env.d.ts` → 讓 TypeScript 認得 Vite 與 Vue 特殊檔案/變數。
        - `vite.config.ts` → 控制 Vite 的行為（插件、路徑、伺服器、建置方式）。
    - 專案架構
    ```text
    front/                            ← 專案根目錄
    ├─ public/                       ← 靜態檔（原樣輸出）：favicon、robots.txt、不可經由打包流程的資源
    ├─ src/
    │   ├─ app/                      ← 框架層（不含業務）：路由、全域狀態入口、Layout、全域樣式
    │   │   ├─ router/               ← Vue Router 設定與路由表
    │   │   ├─ stores/               ← Pinia 的全域 store（僅跨功能的狀態）
    │   │   ├─ layouts/              ← 頁面骨架（Header/Sidebar/Main）
    │   │   └─ styles/               ← 全域 SCSS（變數、reset、utilities）
    │   ├─ features/                 ← 功能域（業務邏輯的家，分批搬遷重點）
    │   │   └─ <featureA>/
    │   │       ├─ pages/            ← 路由頁（每頁一檔）
    │   │       ├─ components/       ← 該功能私有元件（僅此功能使用）
    │   │       ├─ api.ts            ← 該功能的 API 呼叫（使用 shared/api/http）
    │   │       ├─ types.ts          ← 該功能的型別（DTO、ViewModel）
    │   │       └─ index.ts          ← 導出整個功能對外暴露的接口（可選）
    │   ├─ shared/                   ← 跨功能可重用的東西（謹慎放）
    │   │   ├─ components/           ← 基礎/無業務樣式元件（BaseButton、Modal）
    │   │   ├─ api/                  ← http 客戶端、攔截器、通用錯誤處理
    │   │   ├─ composables/          ← 可重用 hooks（useDebounce、usePagination）
    │   │   ├─ utils/                ← 工具函式（日期、字串、校驗）
    │   │   ├─ constants/            ← 常數（枚舉、字典）
    │   │   └─ types/                ← 跨功能共用型別（User、Auth 等）
    │   ├─ assets/                   ← 需經由打包的資產（會被 hash）：圖片、字型、樣式片段
    │   ├─ App.vue
    │   └─ main.ts
    ├─ .env.development              ← 開發環境變數（VITE_ 前綴）
    ├─ .env.production               ← 正式環境變數
    ├─ index.html
    ├─ tsconfig.json                 ← TypeScript 編譯設定
    ├─ env.d.ts                      ← Vue 檔/環境變數型別宣告
    ├─ vite.config.ts                ← Vite 設定（plugins、alias、proxy、CSS）
    ├─ eslint.config.js              ← 程式風格/規範（可加 Prettier）
    ├─ tests/                        ← 單元/端對端測試（可選）
    └─ Dockerfile / docker-compose.yml（部署用，可選）
    ```
- 安裝 pinia axios sass vue-router@^4
    - npm i vue@^3 vue-router@^4 pinia axios sass
- 使用 "功能" 作為資料夾分類依據
- `npm i -D axios-mock-adapter`
    - 模擬不同登入角色，做畫面測試

## back-end
- 安裝 Temurin 21 LTS
    - choco install temurin21 -y
    - choco install maven -y
- 使用 Docker-compose 將資料庫以及後端一起建立
    - 使用 flyway 或其他 db migration 工具建立初始資料庫

- 專案架構
```
backend/
├─ pom.xml
├─ Dockerfile                      # 產品版映像（dev 用 compose 已能直跑）
├─ src/
│  ├─ main/
│  │  ├─ java/ntcu/selab/springserver/
│  │  │  ├─ Application.java
│  │  │  ├─ config/               # 組態（CORS/序列化/共用 Beans）
│  │  │  │   ├─ CorsConfig.java
│  │  │  │   ├─ JacksonConfig.java           (可選)
│  │  │  │   └─ SecurityBeans.java          # PasswordEncoder 等共用 Bean
│  │  │  ├─ security/             # JWT 與 Security 設定
│  │  │  │   ├─ SecurityConfig.java         # SecurityFilterChain、白名單
│  │  │  │   ├─ JwtUtils.java               # 產/解 JWT
│  │  │  │   └─ JwtAuthFilter.java          # 驗證 Bearer Token
│  │  │  ├─ domain/               # 實體模型（和資料表對齊）
│  │  │  │   ├─ user/User.java
│  │  │  │   ├─ course/Course.java
│  │  │  │   └─ enrollment/
│  │  │  │       ├─ Enrollment.java
│  │  │  │       └─ EnrollmentId.java       # 複合主鍵
│  │  │  ├─ repository/           # 資料存取（只碰資料庫）
│  │  │  │   ├─ UserRepository.java
│  │  │  │   ├─ CourseRepository.java
│  │  │  │   └─ EnrollmentRepository.java
│  │  │  ├─ service/              # 業務邏輯（交易、權限、規則）
│  │  │  │   ├─ AuthService.java
│  │  │  │   └─ CourseService.java
│  │  │  ├─ web/                  # Web 介面層：Controller + DTO + Mapper
│  │  │  │   ├─ controller/
│  │  │  │   │   ├─ AuthController.java
│  │  │  │   │   └─ CourseController.java
│  │  │  │   ├─ dto/
│  │  │  │   │   ├─ auth/LoginRequest.java
│  │  │  │   │   ├─ auth/LoginResponse.java
│  │  │  │   │   ├─ user/UserDto.java
│  │  │  │   │   └─ course/CourseDto.java
│  │  │  │   └─ mapper/
│  │  │  │       └─ CourseMapper.java
│  │  │  ├─ exception/            # 一致化錯誤處理
│  │  │  │   ├─ ApiException.java
│  │  │  │   └─ GlobalExceptionHandler.java
│  │  │  └─ util/                 # 共用工具（非業務）
│  │  │      └─ Strings.java      (可選)
│  │  └─ resources/
│  │     ├─ application.yml
│  │     ├─ application-dev.yml
│  │     ├─ db/
│  │     │  └─ migration/
│  │     │      ├─ V1__init.sql           # 建 users/courses + 初始資料
│  │     │      └─ V2__seed.sql           # 建 enrollments + seed 關聯
│  │     └─ banner.txt                    (可選)
│  └─ test/
│     └─ java/ntcu/selab/springserver/
│         ├─ web/controller/CourseControllerTest.java  # Web 層測試（MockMvc）
│         └─ service/CourseServiceTest.java            # 服務層單元測試
```
- springboot 後端開發順序
    0. 初始化與基本設定(application-dev.yml：資料庫連線、Actuator、Flyway 設定。)
    1. Domain 與 Repository（先把資料模型固定住，完成 Entity 與 Repository）
        - 定義 Entity（JPA 對欄位與關聯的宣告）必須與資料庫的欄位對齊
        - 在 Repository 撰寫實際資料存取介面(透過衍生查詢, JPQL, 原生 SQL)
        - 繼承 JpaRepository 之後，你就自動擁有一堆 CRUD 方法，例如：
            - findAll() → 查全部
            - findById(id) → 依主鍵查
            - save(entity) → 新增/更新
            - deleteById(id) → 刪除
    2. 資料庫遷移（Flyway）與初始資料
    3. DTO 與 Mapper（決定對外格式）
        - Entity (Domain) → 資料庫的真實結構。
        - DTO → API 的輸入輸出格式。
        - Mapper → 把 Entity ↔ DTO 做轉換，避免洩漏內部資料，也讓 API 與 DB 解耦
    4. Service（業務邏輯）
    5. Security 與 JWT（無狀態保護 API)
    6. Controller（對外 API）
    - 不要隨便加 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci


- 部署正式流程

(1) 程式碼層
- 後端（Spring Boot）
  - 統一環境變數（不要寫死在 yml）
    - SPRING_PROFILES_ACTIVE=prod
    - DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
    - APP_JWT_SECRET（≥64 bytes，你程式已檢查）
    - APP_JWT_EXP_MINUTES（可選）
    -（同網域反代）SERVER_SERVLET_CONTEXT_PATH=/api
  - 健康檢查端點
    - 開啟 Actuator：management.endpoints.web.exposure.include=health,info
    - 內部 probe 使用 /api/actuator/health（或你自定 /health，二擇一統一）
  - CORS
    - 正式建議不要跨域：用 Ingress 把 /api 反代到後端。這樣後端 CORS 設定可最小化（甚至關掉）。
  - 安全/錯誤
    - 保持 ApiEnvelope 一致；@ControllerAdvice 統一錯誤格式。
    - JwtAuthFilter 跳過 OPTIONS、/auth/**，並加在 UsernamePasswordAuthenticationFilter 前。
    - 密碼一律 Bcrypt；移除明碼 fallback。
  - 資料遷移
    - 使用 Flyway/Liquibase；src/main/resources/db/migration/ 放版控 SQL。
- 前端（Vite）
    - 環境檔
      - .env.production：VITE_API_BASE_URL=/api（同網域反代）
      - 路由
        - createWebHistory(import.meta.env.BASE_URL)；Nginx/Ingress 設 history fallback。
      - HTTP 客戶端
        - Axios 攔截器：自動夾帶 Authorization: Bearer <token>、401 自動導回 /login、統一解包 ApiEnvelope。
      - 安全
        - 移除多餘 console.log；避免 v-html（或加 DOMPurify）。
        - Token 存 localStorage → 嚴格避免 XSS；必要時後端加 CSP。

(2) 容器化（Docker）
- 後端 Dockerfile（多階段）
    - 第一段用 maven build，第二段用 JRE 運行；以 JAVA_OPTS 作 JVM 調整（記憶體等）。
    - 僅 EXPOSE 8080；ENTRYPOINT 用 java -jar app.jar。
- 前端 Dockerfile（build + Nginx serve）
    - Build 階段：npm ci && npm run build
    - Serve 階段：Nginx 映像，拷貝 dist。
    - nginx.conf 設 try_files $uri /index.html;（history fallback）。
    - 注意：容器內不寫入應用目錄，避免 stateful；上傳/匯出等需外部儲存或後端提供下載流。

(3) 建置與映像（CI/CD）
- 建置腳本：
    - 後端：mvn -DskipTests package → docker build -t <registry>/backend:<tag>
    - 前端：npm ci && npm run build → docker build -t <registry>/frontend:<tag>
- 推送：docker push 到私有/公有 registry。
- 標籤策略：使用語義版（1.0.0）＋不可變 digest；禁止覆蓋 latest 在正式環境使用。

(4) Kubernetes 組態（YAML/Helm/Kustomize）
- Namespace
    - 建 app-prod 命名空間。
- Secret / ConfigMap
    - Secret：DB_USER, DB_PASSWORD, APP_JWT_SECRET（不要 commit；可用 Sealed Secrets/External Secrets）
    - ConfigMap：SPRING_PROFILES_ACTIVE, DB_HOST, DB_PORT, DB_NAME, APP_JWT_EXP_MINUTES, SERVER_SERVLET_CONTEXT_PATH
- 注意：JWT 秘鑰務必≥64 bytes；若太短你現有程式會拋例外。

- MySQL（擇一）
    - 推薦：雲端託管（RDS/Cloud SQL），K8s 只連線；減少 Stateful 管理負擔。
    - 自管：StatefulSet + PVC；設定 utf8mb4、持久卷，備份策略另行規劃。

- Backend Deployment + Service
    - replicas: 2 起步，設 resources.requests/limits（例如 250m/1CPU、512Mi/1Gi）。
    - readinessProbe/livenessProbe 指向 /api/actuator/health；初始延遲合理。
    - envFrom 掛 ConfigMap 與 Secret。
    - Service 用 ClusterIP。

- Frontend Deployment + Service
    - replicas: 2；靜態檔服務。
    - Probe 指向 /。
    - Service 用 ClusterIP。

- Ingress（以 Nginx Ingress Controller 為例）
    - 同網域：
        - / → 前端 web-frontend:80
        - /api → 後端 spring-backend:8080
        - 若啟用 TLS：加上 cert-manager（ClusterIssuer）自動簽發/續期。

- 注意：
    -  設 proxy-read-timeout / proxy-send-timeout（長請求時）
    - 若有大檔下載，上調 client_max_body_size（Nginx）。

(5) 資料庫遷移流程
- 選 A（最簡單）：讓 Spring 啟動自動執行 Flyway（有鎖，併發安全）。
- 選 B（更可控）：K8s Job 先跑 migration 成功後，再 rollout 後端。
- 確保多副本下不會重複寫 schema（Flyway 預設會鎖，但請在實際 DB 驗證一次）。

(6) 監控、日誌、擴縮
- HPA：CPU 60% 目標，自動擴縮 2~6 個 Pod（依流量調整）。
- 日誌：輸出 STDOUT（JSON 更佳），集成 EFK/雲端 log。
- 監控：Prometheus/Grafana；後端可加 /actuator/prometheus。
- 追蹤（選配）：OpenTelemetry/Jaeger。

(7) 正式環境安全細節
- CORS：走同網域 /api 反代，避免瀏覽器跨域。
- Headers：前端 Nginx 加安全標頭（CSP/Strict-Transport-Security/X-Frame-Options/Referrer-Policy 等）。
- Token：前端持在 LS → 嚴控 XSS（不使用 v-html；必要時 DOMPurify；後端 CSP）。
- 網路：NetworkPolicy 限制後端只被 Ingress/前端訪問、只出站到 DB。
- 鑰輪替：JWT secret 定期輪替（滾動部署）；DB 密碼也要有換發計畫。
- 檔案上傳：限制大小與副檔名；後端做 MIME 驗證與病毒掃描（若必要）。

(8) 部署與驗證步驟
- 套用 Namespace/Secrets/ConfigMap：kubectl apply -f ns.yaml && -f secrets.yaml && -f config.yaml
-（如需）部署 MySQL 或確認雲端 DB 存取白名單。
- 部署 Backend / Frontend：kubectl apply -f backend.yaml -f frontend.yaml
- 部署 Ingress + TLS：kubectl apply -f ingress.yaml
- 驗證：
    - kubectl -n app-prod get pods,svc,ing
    - kubectl -n app-prod logs deploy/spring-backend（無錯誤）
    - 瀏覽器開 https://你的域名/、打 API https://你的域名/api/health 或 /api/actuator/health
- 壓測與調參：視情況調整 HPA、資源限制、連線池（DataSource pool size）。


- Kustomize
    - 先寫一組通用的 Kubernetes YAML（base），再用 overlay 在不同環境（dev、staging、prod）做少量覆蓋（patch）、替換（image tag）、加註（label/annotation）等，最後由 kubectl apply -k 一次套用。
    - 注意 : Kustomize中的resources只會吃最後一個設定

    

- 安裝資料庫
    - 安裝helm
        - sudo snap install helm --classic
    - 加入Bitnami reop
        - helm repo add bitnami https://charts.bitnami.com/bitnami
        - helm repo update
        - helm repo list
    - 安裝 mysql
        - stateful + service
        - docker pull bitnamilegacy/mysql:9.4.0-debian-12-r1
        - kind load docker-image bitnamilegacy/mysql:9.4.0-debian-12-r1 --name dev
        - kubectl apply -f mysql.yaml
    - 注意 Cros
        - Cros 只有在跨網域時才會啟動，如果用 hostport 他們視為同 Origin
        - cfg.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://localhost:5173"));
    - ingress 使用 hostPort + DaemonSet 模式

- 使用 ArgoCD 自動進行 k8s 部署
    - kubectl create namespace argocd
    - kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
    - kubectl -n argocd get secret argocd-initial-admin-secret   -o jsonpath="{.data.password}" | base64 -d (取得初始帳號 admin 密碼 6drZd5rt6g6PKT9d)
