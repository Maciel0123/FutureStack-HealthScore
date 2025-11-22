# **FutureStack – Health Score API**

O FutureStack – Health Score API é uma plataforma que calcula um índice de bem-estar profissional (0 a 1000) baseado em check-ins diários do usuário sobre humor, energia, sono, foco e carga de trabalho. Esse score mostra o nível de equilíbrio do profissional e o risco de burnout, combinando IA Generativa com Spring AI para oferecer recomendações personalizadas. A solução integra mensageria assíncrona, caching, internacionalização e segurança completa com JWT.

---

## 📌 **Tecnologias Utilizadas**

- **Java 21**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **Bean Validation**
- **Spring Cache**
- **Internacionalização** (pt-BR e en-US)
- **RabbitMQ** (Mensageria)
- **Spring AI** (Groq)
- **Maven**

---

## 🧠 **Funcionalidades Principais**

- 🔐 **Autenticação & Autorização** com JWT
- 📊 **Check-ins Diários** para monitoramento do bem-estar
- 🎯 **Cálculo Automático de Score** (0-1000 pontos)
- 🤖 **Recomendações Personalizadas** via IA Generativa
- 💬 **Chat de Suporte Emocional** com IA
- ⚡ **Processamento Assíncrono** com RabbitMQ
- 🚀 **Cache** para otimização de performance
- 🌍 **Internacionalização** (pt-BR e en-US)
- 📈 **Relatórios e Métricas** (média mensal, resumos)

---

## 📚 **Documentação**

### 🔐 **Autenticação**

#### **Cadastrar Usuário**
**POST** `/users`

**Request:**
```json
{
  "email": "Helena@example.com",
  "password": "123456",
  "nomeUser": "Helena",
  "phone": "1198787768"
}
```

#### **Login**
**POST** `/login`

**Request:**
```json
{
  "email": "Helena@example.com",
  "password": "123456"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6...",
  "type": "Bearer",
"email": "Helena@example.com"
}
```

**Use o token em todas as requisições:**
```
Authorization: Bearer SEU_TOKEN_JWT
```

---

### 👤 **Gerenciamento de Usuário**

#### **Atualizar Usuário**
**PUT** `/users/{id}`

**Request:**
```json
{
  "nome": "Amanda Nunes",
  "email": "amanda.nova@example.com"
}
```

#### **Deletar Usuário**
**DELETE** `/users/{id}`

#### **Listar Usuários**
**GET** `/users`

---

### 📊 **Check-ins e Monitoramento**

#### **Criar Check-in**
**POST** `/checkins`

**Request:**
```json
{
  "mood": 6,
  "energy": 7,
  "sleep": 6,
  "focus": 8,
  "hoursWorked": 5
}
```

**Response:**
```json
{
  "id": 42,
  "date": "2025-11-21",
  "score": 720,
  "mood": 6,
  "energy": 7,
  "sleep": 6,
  "focus": 8,
  "hoursWorked": 5,
"message": "Bom! Continue mantendo o equil�brio."
}
```

#### **Listar Check-in**
**GET** `/checkins`

#### **Atualizar Check-in**
**PUT** `/checkins/{id}`

**Request:**
```json
{
  "mood": 8,
  "energy": 8,
  "sleep": 6,
  "focus": 8,
  "hoursWorked": 7
}
```
#### **Deletar Check-in**
**DELETE** `/checkins/{id}`

#### **Último Check-in**
**GET** `/checkins/last`

#### **Média Mensal**
**GET** `/checkins/weekly-average`

---

### 🤖 **IA Generativa & Recomendações**

#### **Recomendação Diária**
**GET** `/api/ai/daily`

**Response:**
```json
Com humor equilibrado, energia boa, sono estável, foco forte e carga de trabalho moderada, seu desempenho está em excelente nível, refletindo um equilíbrio saudável entre produtividade e bem‑estar. Essa combinação favorece a clareza de pensamento e a resistência ao estresse, permitindo que você alcance resultados consistentes. Mantenha essa rotina, pois sustenta a qualidade do seu trabalho e a satisfação pessoal. Reserve 10 minutos para alongamento e respiração profunda antes de cada bloco de trabalho.
```

#### **Resumo Semanal/Mensal**
**GET** `/api/ai/monthly-summary`

#### **Chat com IA**
**POST** `/chat`

**Request:**
```json
{
  "message": "Estou muito cansada hoje, como posso equilibrar minha energia?"
}
```

**Response:**
```json
{
  "response": "Percebo que seus níveis recentes de energia estão baixos. Tente programar pequenas pausas ao longo do dia..."
}
```

---

## 🌍 **Internacionalização (i18n)**

A API suporta **português (pt-BR)**, **inglês (en-US)**, **Espanhol (es-ES)**

**Exemplo de uso:**
```bash
# Português (padrão)
GET /checkins/last?lang=pt_BR

# Inglês
GET /checkins/last?lang=en_US

# Inglês
GET /checkins/last?lang=es_ES
```

---

## ⚡ **Arquitetura & Mensageria**

### **Fluxo de Check-in com RabbitMQ**
1. ✅ Usuário envia check-in
2. 📨 Evento é publicado na fila `CHECKIN_QUEUE`
3. 🤖 Consumer processa e gera recomendação via IA
4. 💾 Recomendação é salva no banco
5. 🔔 Usuário recebe recomendação personalizada

**Estrutura do Evento:**
```json
{
  "userId": 1,
  "checkInId": 42,
  "score": 720,
  "timestamp": "2025-11-21"
}
```

---

## 🐳 **Deploy:**
- **Link para acesso:**: [Download Link](https://futurestack-healthscore.onrender.com)
---

## 📱 **Integração Mobile**

Este backend é consumido pelo aplicativo mobile em React Native:

- **APK**: [Download Link](https://expo.dev/accounts/marychriss/projects/gs2-futureStack-HealthScore/builds/0037f534-50e1-4420-8f86-19c818531244)

---

## 🎬 **Vídeos e Demonstrações**

🎯 **Vídeo Pitch**: [Link para o vídeo pitch](https://youtu.be/i2YS-esLwMg?si=UDDCjzt3UO7S_OPe)  
📱 **Vídeo Demonstração**: [Link](https://youtu.be/Q4rdv_7zLEI?si=tRc9SSRSrqiVqWpY)  

---

## 👥 **Equipe de Desenvolvimento**

| Integrante | RM |
|------------|-----|
| **Mariana Christina** | RM554773 |
| **Gabriela Moguinho** |RM556143 |
| **Henrique Maciel** | RM556480 |


