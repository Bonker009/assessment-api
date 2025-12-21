// Postman Collection Scripts
// Copy these scripts to Postman's Pre-request Script or Test Script sections

// ============================================
// PRE-REQUEST SCRIPTS
// ============================================

// Auto-refresh token if expired
function autoRefreshToken() {
    const token = pm.environment.get("accessToken");
    const refreshToken = pm.environment.get("refreshToken");
    
    if (!token && refreshToken) {
        pm.sendRequest({
            url: pm.environment.get("baseUrl") + "/api/v4/auth/refresh",
            method: "POST",
            header: {
                "Content-Type": "application/json"
            },
            body: {
                mode: "raw",
                raw: JSON.stringify({
                    refreshToken: refreshToken
                })
            }
        }, function (err, res) {
            if (!err && res.code === 200) {
                const jsonData = res.json();
                if (jsonData.data && jsonData.data.accessToken) {
                    pm.environment.set("accessToken", jsonData.data.accessToken);
                }
            }
        });
    }
}

// Validate required environment variables
function validateEnvironment() {
    const baseUrl = pm.environment.get("baseUrl");
    if (!baseUrl) {
        console.error("baseUrl is not set in environment");
        return false;
    }
    return true;
}

// Set random UUID for testing
function setRandomUUID(variableName) {
    const uuid = generateUUID();
    pm.environment.set(variableName, uuid);
    return uuid;
}

function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

// ============================================
// TEST SCRIPTS
// ============================================

// Common response validation
function validateApiResponse() {
    pm.test("Response has success field", function () {
        const jsonData = pm.response.json();
        pm.expect(jsonData).to.have.property('success');
    });

    pm.test("Response has statusCode field", function () {
        const jsonData = pm.response.json();
        pm.expect(jsonData).to.have.property('statusCode');
    });

    pm.test("Response has message field", function () {
        const jsonData = pm.response.json();
        pm.expect(jsonData).to.have.property('message');
    });

    pm.test("Response has timestamp field", function () {
        const jsonData = pm.response.json();
        pm.expect(jsonData).to.have.property('timestamp');
    });
}

// Validate success response
function validateSuccessResponse() {
    pm.test("Status code is 2xx", function () {
        pm.expect(pm.response.code).to.be.oneOf([200, 201, 204]);
    });

    pm.test("Response indicates success", function () {
        const jsonData = pm.response.json();
        pm.expect(jsonData.success).to.be.true;
    });
}

// Validate error response
function validateErrorResponse(expectedStatus) {
    pm.test("Status code is " + expectedStatus, function () {
        pm.response.to.have.status(expectedStatus);
    });

    pm.test("Response indicates failure", function () {
        const jsonData = pm.response.json();
        pm.expect(jsonData.success).to.be.false;
    });
}

// Save exam ID from response
function saveExamId() {
    const jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.assessmentId) {
        pm.environment.set("examId", jsonData.data.assessmentId);
        console.log("Saved examId: " + jsonData.data.assessmentId);
    }
}

// Save student assessment ID from response
function saveStudentAssessmentId() {
    const jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.assessmentId) {
        pm.environment.set("studentAssessmentId", jsonData.data.assessmentId);
        console.log("Saved studentAssessmentId: " + jsonData.data.assessmentId);
    }
}

// Validate exam response structure
function validateExamResponse() {
    pm.test("Exam response has required fields", function () {
        const jsonData = pm.response.json();
        if (jsonData.data) {
            pm.expect(jsonData.data).to.have.property('assessmentId');
            pm.expect(jsonData.data).to.have.property('name');
            pm.expect(jsonData.data).to.have.property('isQuiz');
            pm.expect(jsonData.data).to.have.property('schedule');
            pm.expect(jsonData.data).to.have.property('totalSections');
            pm.expect(jsonData.data).to.have.property('totalQuestions');
        }
    });
}

// Validate student assessment response structure
function validateStudentAssessmentResponse() {
    pm.test("Student assessment response has required fields", function () {
        const jsonData = pm.response.json();
        if (jsonData.data) {
            pm.expect(jsonData.data).to.have.property('studentId');
            pm.expect(jsonData.data).to.have.property('assessmentId');
            pm.expect(jsonData.data).to.have.property('status');
            pm.expect(jsonData.data).to.have.property('score');
        }
    });
}

// Check response time
function checkResponseTime(maxTime) {
    pm.test("Response time is less than " + maxTime + "ms", function () {
        pm.expect(pm.response.responseTime).to.be.below(maxTime);
    });
}

// Validate date format
function validateDateFormat(dateString, format) {
    if (format === "YYYY-MM-DD") {
        const regex = /^\d{4}-\d{2}-\d{2}$/;
        return regex.test(dateString);
    }
    if (format === "HH:mm:ss") {
        const regex = /^\d{2}:\d{2}:\d{2}$/;
        return regex.test(dateString);
    }
    return false;
}

// Extract and save token from login response
function extractTokenFromLogin() {
    const jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.accessToken) {
        pm.environment.set("accessToken", jsonData.data.accessToken);
        console.log("Access token saved");
    }
    if (jsonData.data && jsonData.data.refreshToken) {
        pm.environment.set("refreshToken", jsonData.data.refreshToken);
        console.log("Refresh token saved");
    }
}

// ============================================
// UTILITY FUNCTIONS
// ============================================

// Format date to YYYY-MM-DD
function formatDate(date) {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Get future date (days from now)
function getFutureDate(days) {
    const date = new Date();
    date.setDate(date.getDate() + days);
    return formatDate(date);
}

// Get current time in HH:mm:ss format
function getCurrentTime() {
    const now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
}

// Add hours to time
function addHoursToTime(timeString, hours) {
    const [h, m, s] = timeString.split(':').map(Number);
    const date = new Date();
    date.setHours(h + hours, m, s);
    return date.toTimeString().slice(0, 8);
}

// ============================================
// EXAMPLE USAGE IN POSTMAN
// ============================================

/*
PRE-REQUEST SCRIPT EXAMPLE:
---------------------------
// Validate environment
if (!validateEnvironment()) {
    throw new Error("Environment not properly configured");
}

// Auto-refresh token
autoRefreshToken();
*/

/*
TEST SCRIPT EXAMPLE:
-------------------
// Common validations
validateApiResponse();
validateSuccessResponse();
checkResponseTime(2000);

// Save IDs
saveExamId();

// Specific validations
validateExamResponse();
*/

/*
DYNAMIC REQUEST BODY EXAMPLE:
----------------------------
// In request body (raw JSON), use:
{
    "name": "Test Exam",
    "schedule": {
        "assessmentDate": "{{$randomDate}}",
        "startTime": "09:00:00",
        "endTime": "11:00:00"
    }
}

// Or use Postman dynamic variables:
- {{$randomUUID}} - Random UUID
- {{$randomInt}} - Random integer
- {{$timestamp}} - Current timestamp
- {{$randomDate}} - Random date
*/

