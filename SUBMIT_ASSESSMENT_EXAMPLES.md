# Submit Assessment with Answers - Request Body Examples

## Endpoint
`POST /api/v4/student-assessments/submit-with-answers`

## Authentication
Requires Bearer token with `student` role.

## Request Body Structure

```json
{
  "assessmentId": "uuid-of-assessment",
  "score": 85.5,
  "durationInMinute": 45.5,
  "answers": [
    {
      "questionId": "uuid-of-question-1",
      "assessmentId": "uuid-of-assessment",
      "answer": { /* answer content based on question type */ },
      "score": 10.0
    }
  ]
}
```

## Answer Format by Question Type

### 1. MCQ (Multiple Choice Question)

For MCQ questions, the answer should contain the selected option index (0-based).

**Question Example:**
- Question: "What is 2+2?"
- Options: ["2", "3", "4", "5"]
- Correct answer: index 2 (which is "4")

**Answer Request:**
```json
{
  "questionId": "550e8400-e29b-41d4-a716-446655440001",
  "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
  "answer": {
    "selectedOption": 2
  },
  "score": 10.0
}
```

### 2. TRUE_FALSE Question

For True/False questions, the answer should contain a boolean value.

**Question Example:**
- Question: "Java is a programming language"
- Correct answer: true

**Answer Request:**
```json
{
  "questionId": "550e8400-e29b-41d4-a716-446655440002",
  "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
  "answer": {
    "value": true
  },
  "score": 5.0
}
```

### 3. LONG_ANSWER Question

For Long Answer questions, the answer should contain the text response.

**Question Example:**
- Question: "Explain the concept of polymorphism"

**Answer Request:**
```json
{
  "questionId": "550e8400-e29b-41d4-a716-446655440003",
  "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
  "answer": {
    "text": "Polymorphism is a core concept in object-oriented programming that allows objects of different types to be accessed through the same interface. It enables a single interface to represent different underlying forms (data types)."
  },
  "score": 15.0
}
```

### 4. CODING Question

For Coding questions, the answer should contain the code solution.

**Question Example:**
- Question: "Write a function to reverse a string"
- Language: "java"

**Answer Request:**
```json
{
  "questionId": "550e8400-e29b-41d4-a716-446655440004",
  "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
  "answer": {
    "code": "public String reverseString(String str) {\n    StringBuilder reversed = new StringBuilder();\n    for (int i = str.length() - 1; i >= 0; i--) {\n        reversed.append(str.charAt(i));\n    }\n    return reversed.toString();\n}",
    "language": "java"
  },
  "score": 20.0
}
```

## Complete Example Request Body

Here's a complete example with multiple question types:

```json
{
  "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
  "score": 85.5,
  "durationInMinute": 45.5,
  "answers": [
    {
      "questionId": "550e8400-e29b-41d4-a716-446655440001",
      "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
      "answer": {
        "selectedOption": 2
      },
      "score": 10.0
    },
    {
      "questionId": "550e8400-e29b-41d4-a716-446655440002",
      "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
      "answer": {
        "value": true
      },
      "score": 5.0
    },
    {
      "questionId": "550e8400-e29b-41d4-a716-446655440003",
      "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
      "answer": {
        "text": "Polymorphism is a core concept in object-oriented programming..."
      },
      "score": 15.0
    },
    {
      "questionId": "550e8400-e29b-41d4-a716-446655440004",
      "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
      "answer": {
        "code": "public String reverseString(String str) {\n    return new StringBuilder(str).reverse().toString();\n}",
        "language": "java"
      },
      "score": 20.0
    }
  ]
}
```

## Field Descriptions

### SubmitAnswersRequest
- **assessmentId** (UUID, required): The ID of the assessment being submitted
- **score** (Double, required): The total score achieved by the student (must be >= 0)
- **durationInMinute** (Double, required): The time taken to complete the assessment in minutes (must be >= 0)
- **answers** (List<AnswerRequest>, required): List of answers for all questions

### AnswerRequest
- **questionId** (UUID, required): The ID of the question being answered
- **assessmentId** (UUID, required): The ID of the assessment (should match the parent assessmentId)
- **answer** (Map<String, Object>, required): The answer content, structure varies by question type:
  - **MCQ**: `{"selectedOption": <integer>}` - 0-based index of selected option
  - **TRUE_FALSE**: `{"value": <boolean>}` - true or false
  - **LONG_ANSWER**: `{"text": "<answer text>"}` - free text response
  - **CODING**: `{"code": "<code>", "language": "<language>"}` - code solution and language
- **score** (Double, optional): The score for this individual answer (can be null if auto-grading)

## Notes

1. **Answer Map Structure**: The `answer` field is a flexible `Map<String, Object>` that can accommodate different question types. The keys used above are examples - you can use any structure that fits your needs.

2. **Score Calculation**: 
   - The `score` in each `AnswerRequest` is optional and represents the score for that individual answer
   - The `score` in `SubmitAnswersRequest` is the total score for the entire assessment
   - If individual answer scores are not provided, the system may use the total score

3. **Assessment ID Consistency**: The `assessmentId` in each `AnswerRequest` should match the `assessmentId` in the parent `SubmitAnswersRequest`.

4. **Question IDs**: Make sure all `questionId` values correspond to actual questions in the assessment.

5. **Duration**: The `durationInMinute` should reflect the actual time the student spent on the assessment.

## Response Example

```json
{
  "success": true,
  "statusCode": 200,
  "message": "Assessment and answers submitted successfully",
  "data": {
    "studentId": "550e8400-e29b-41d4-a716-446655440010",
    "assessmentId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "SUBMITTED",
    "score": 85.5,
    "totalScore": 85.5,
    "durationInMinute": 45.5,
    "joinAt": "2024-12-22T10:00:00",
    "submittedAt": "2024-12-22T10:45:30",
    "gradingStatus": "pending"
  },
  "timestamp": "2024-12-22T10:45:30"
}
```

