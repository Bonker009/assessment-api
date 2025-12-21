# Postman Collection Setup Guide

## Quick Start

1. **Import Collection**
   - Open Postman
   - Click "Import" button
   - Select `Assessment_API.postman_collection.json`
   - Click "Import"

2. **Import Environment**
   - Click "Import" again
   - Select `Assessment_API.postman_environment.json`
   - Click "Import"
   - Select "Assessment API - Local" from the environment dropdown (top right)

3. **Update Base URL** (if needed)
   - Click on the environment name
   - Edit `baseUrl` if your server is not running on `http://localhost:8080`

## Authentication Flow

1. **Register a User** (Optional - if user doesn't exist)
   - Go to "Authentication" → "Register User"
   - Update the request body with your user details
   - Click "Send"
   - Note: This creates a user in Keycloak

2. **Login**
   - Go to "Authentication" → "Login"
   - Update username and password in the request body
   - Click "Send"
   - The access token will be automatically saved to the environment variable `accessToken`
   - All subsequent requests will use this token automatically

## Using the Collection

### Exam Management

1. **Create Exam**
   - Go to "Exam Management" → "Create Exam"
   - The exam ID will be automatically saved to `examId` variable
   - Update the request body with your exam details

2. **Get All Exams**
   - Returns list of all exams (without sections/questions for performance)

3. **Get Active Exams**
   - Returns only exams that are currently active (published and within time window)

4. **Get Exam by ID**
   - Returns full exam details including sections and questions
   - Uses `{{examId}}` variable automatically

5. **Publish/Unpublish Exam**
   - Must have schedule set before publishing
   - Cannot publish after exam start time

### Student Assessment Flow

1. **Assign Assessment** (Teacher/Admin only)
   - Assigns an exam to a student
   - Requires `studentId` and `examId` in request body
   - Set `studentId` in environment variables first

2. **Start Assessment** (Student only)
   - Student starts taking the exam
   - Exam must be published and active

3. **Submit Assessment** (Student only)
   - Student submits their answers
   - Includes score and duration

4. **Grade Assessment** (Teacher/Admin only)
   - Teacher grades the submitted assessment

## Environment Variables

The collection uses these environment variables:

- `baseUrl`: API base URL (default: http://localhost:8080)
- `accessToken`: JWT token (auto-set after login)
- `refreshToken`: Refresh token (auto-set after login)
- `examId`: Current exam ID (auto-set after creating exam)
- `studentId`: Student UUID (set manually)
- `sectionId`: Section UUID (set manually if needed)
- `questionId`: Question UUID (set manually if needed)
- `studentAssessmentId`: Student assessment ID (auto-set after assignment)

## Test Scripts

The collection includes automated test scripts:

- **Login**: Automatically saves access token
- **Create Exam**: Automatically saves exam ID
- **Start Assessment**: Validates status is IN_PROGRESS

## Request Examples

### Create Exam with Sections and Questions

```json
{
    "name": "Midterm Exam 2024",
    "isQuiz": false,
    "subjectId": "550e8400-e29b-41d4-a716-446655440000",
    "schedule": {
        "assessmentDate": "2024-12-20",
        "startTime": "09:00:00",
        "endTime": "11:00:00",
        "isPublished": false
    },
    "sections": [
        {
            "sectionName": "Section 1: Multiple Choice",
            "questions": [
                {
                    "questionType": "MCQ",
                    "questionContent": {
                        "question": "What is 2+2?",
                        "options": ["2", "3", "4", "5"],
                        "correct": 2
                    },
                    "points": 10.0
                }
            ]
        }
    ]
}
```

### Question Types

1. **MCQ (Multiple Choice)**
```json
{
    "questionType": "MCQ",
    "questionContent": {
        "question": "What is 2+2?",
        "options": ["2", "3", "4", "5"],
        "correct": 2
    },
    "points": 10.0
}
```

2. **TRUE_FALSE**
```json
{
    "questionType": "TRUE_FALSE",
    "questionContent": {
        "question": "Java is a programming language",
        "correct": true
    },
    "points": 5.0
}
```

3. **LONG_ANSWER**
```json
{
    "questionType": "LONG_ANSWER",
    "questionContent": {
        "question": "Explain the concept of polymorphism"
    },
    "points": 15.0
}
```

4. **CODING**
```json
{
    "questionType": "CODING",
    "questionContent": {
        "question": "Write a function to reverse a string",
        "language": "java"
    },
    "points": 20.0
}
```

## Troubleshooting

### 401 Unauthorized
- Make sure you've logged in and the token is set
- Check if token has expired (login again)
- Verify the token is being sent in the Authorization header

### 403 Forbidden
- Check user roles (teacher/admin for exam management, student for assessment operations)
- Verify the user has the correct role in Keycloak

### 404 Not Found
- Verify the ID exists (examId, studentId, etc.)
- Check if the resource was deleted

### 400 Bad Request
- Validate request body format
- Check required fields are present
- Verify date/time formats (YYYY-MM-DD for dates, HH:mm:ss for times)

## Tips

1. **Use Collection Runner**: Run multiple requests in sequence
2. **Save Responses**: Use "Save Response" to create example responses
3. **Duplicate Requests**: Right-click to duplicate and modify requests
4. **Environment Switching**: Create multiple environments for dev/staging/prod
5. **Pre-request Scripts**: Some requests check for tokens automatically

## API Base URL

Default: `http://localhost:8080`

To change:
1. Select environment
2. Edit `baseUrl` variable
3. Save changes

