<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Management System</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        margin: 0;
        padding: 20px;
        background-color: #f5f5f5;
      }
      .container {
        max-width: 1200px;
        margin: 0 auto;
        background-color: white;
        padding: 20px;
        border-radius: 5px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
      }
      h1 {
        color: #333;
        text-align: center;
        margin-bottom: 30px;
      }
      h2 {
        color: #444;
        border-bottom: 1px solid #eee;
        padding-bottom: 10px;
        margin-top: 30px;
      }
      .section {
        margin-bottom: 30px;
        padding: 20px;
        border: 1px solid #ddd;
        border-radius: 5px;
      }
      .form-group {
        margin-bottom: 15px;
      }
      label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
      }
      input[type="text"], input[type="number"] {
        width: 100%;
        padding: 8px;
        border: 1px solid #ddd;
        border-radius: 4px;
        box-sizing: border-box;
      }
      button {
        background-color: #4CAF50;
        color: white;
        padding: 10px 15px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 16px;
        margin-top: 10px;
      }
      button:hover {
        background-color: #45a049;
      }
      table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
      }
      th, td {
        padding: 12px 15px;
        border: 1px solid #ddd;
        text-align: left;
      }
      th {
        background-color: #f2f2f2;
      }
      tr:hover {
        background-color: #f5f5f5;
      }
      .action-buttons button {
        margin-right: 5px;
      }
      .view-btn {
        background-color: #2196F3;
      }
      .view-btn:hover {
        background-color: #0b7dda;
      }
      .edit-btn {
        background-color: #ff9800;
      }
      .edit-btn:hover {
        background-color: #e68a00;
      }
      .delete-btn {
        background-color: #f44336;
      }
      .delete-btn:hover {
        background-color: #da190b;
      }
      .message {
        padding: 10px;
        margin: 10px 0;
        border-radius: 4px;
      }
      .success {
        background-color: #dff0d8;
        border: 1px solid #d6e9c6;
        color: #3c763d;
      }
      .error {
        background-color: #f2dede;
        border: 1px solid #ebccd1;
        color: #a94442;
      }
      #studentDetails {
        padding: 15px;
        background-color: #f9f9f9;
        border: 1px solid #ddd;
        border-radius: 4px;
        margin-top: 15px;
        display: none;
      }
    </style>
</head>
<body>
<div class="container">
    <h1>Student Management System 2 </h1>

    <div id="messageContainer"></div>

    <!-- 1. Show All Students Section -->
    <div class="section">
        <h2>Show All Students</h2>
        <button onclick="getAllStudents()">Show All Students</button>
        <div id="allStudentsTableContainer">
            <table id="allStudentsTable">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Age</th>
                </tr>
                </thead>
                <tbody id="allStudentsTableBody">
                <!-- Student data will be displayed here -->
                </tbody>
            </table>
        </div>
    </div>

    <!-- 2. Add Student Section -->
    <div class="section">
        <h2>Add Student</h2>
        <div class="form-group">
            <label for="addName">Name:</label>
            <input type="text" id="addName" placeholder="Enter student name">
        </div>
        <div class="form-group">
            <label for="addAge">Age:</label>
            <input type="number" id="addAge" placeholder="Enter student age">
        </div>
        <button onclick="addStudent()">Add Student</button>
    </div>

    <!-- 3. Get Student by ID Section -->
    <div class="section">
        <h2>Get Student by ID</h2>
        <div class="form-group">
            <label for="getStudentId">Student ID:</label>
            <input type="number" id="getStudentId" placeholder="Enter student ID">
        </div>
        <button onclick="getStudentById()">Get Student</button>
        <div id="studentDetails">
            <h3>Student Details</h3>
            <p><strong>ID:</strong> <span id="detailId"></span></p>
            <p><strong>Name:</strong> <span id="detailName"></span></p>
            <p><strong>Age:</strong> <span id="detailAge"></span></p>
        </div>
    </div>

    <!-- 4. Update Student Section -->
    <div class="section">
        <h2>Update Student</h2>
        <div class="form-group">
            <label for="updateId">Student ID:</label>
            <input type="number" id="updateId" placeholder="Enter student ID">
        </div>
        <div class="form-group">
            <label for="updateName">New Name (optional):</label>
            <input type="text" id="updateName" placeholder="Enter new name">
        </div>
        <div class="form-group">
            <label for="updateAge">New Age (optional):</label>
            <input type="number" id="updateAge" placeholder="Enter new age">
        </div>
        <button onclick="updateStudent()">Update Student</button>
    </div>

    <!-- 5. Delete Student Section -->
    <div class="section">
        <h2>Delete Student</h2>
        <div class="form-group">
            <label for="deleteId">Student ID:</label>
            <input type="number" id="deleteId" placeholder="Enter student ID">
        </div>
        <button onclick="deleteStudent()">Delete Student</button>
    </div>
</div>

<script>
  // Function to show messages
  function showMessage(message, isError = false) {
    const messageContainer = document.getElementById('messageContainer');
    messageContainer.innerHTML = '<div class="message ' + (isError ? 'error' : 'success') + '">' + message + '</div>';

    // Clear message after 5 seconds
    setTimeout(function() {
      messageContainer.innerHTML = '';
    }, 5000);

    // Scroll to the message
    messageContainer.scrollIntoView({ behavior: 'smooth' });
  }

  // 1. Get all students
  function getAllStudents() {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '<%=request.getContextPath()%>/http-students', true);
    xhr.onload = function() {
      const studentTableBody = document.getElementById('allStudentsTableBody');
      studentTableBody.innerHTML = '';

      if (xhr.status === 200) {
        try {
          const students = JSON.parse(xhr.responseText);

          if (students.length === 0) {
            studentTableBody.innerHTML = '<tr><td colspan="3">No students found</td></tr>';
            return;
          }

          for (let i = 0; i < students.length; i++) {
            const student = students[i];
            const row = document.createElement('tr');
            row.innerHTML =
                '<td>' + student.id + '</td>' +
                '<td>' + student.name + '</td>' +
                '<td>' + student.age + '</td>';
            studentTableBody.appendChild(row);
          }
        } catch (e) {
          showMessage('Error parsing response: ' + e.message, true);
        }
      } else {
        try {
          const response = JSON.parse(xhr.responseText);
          showMessage(response.error || 'Failed to retrieve students', true);
        } catch (e) {
          showMessage('Failed to retrieve students', true);
        }
      }
    };
    xhr.onerror = function() {
      showMessage('Network error occurred', true);
    };
    xhr.send();
  }

  // 2. Add a new student
  function addStudent() {
    const name = document.getElementById('addName').value.trim();
    const age = document.getElementById('addAge').value.trim();

    if (!name || !age) {
      showMessage('Name and age are required', true);
      return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '<%=request.getContextPath()%>/http-students', true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function() {
      if (xhr.status === 201) {
        showMessage('Student created successfully');
        document.getElementById('addName').value = '';
        document.getElementById('addAge').value = '';
        // Refresh the student list
        getAllStudents();
      } else {
        try {
          const response = JSON.parse(xhr.responseText);
          showMessage(response.error || 'Failed to create student', true);
        } catch (e) {
          showMessage('Failed to create student', true);
        }
      }
    };
    xhr.onerror = function() {
      showMessage('Network error occurred', true);
    };
    xhr.send('name=' + encodeURIComponent(name) + '&age=' + encodeURIComponent(age));
  }

  // 3. Get student by ID
  function getStudentById() {
    const id = document.getElementById('getStudentId').value.trim();

    if (!id) {
      showMessage('Student ID is required', true);
      return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open('GET', '<%=request.getContextPath()%>/http-students?id=' + encodeURIComponent(id), true);
    xhr.onload = function() {
      const studentDetails = document.getElementById('studentDetails');

      if (xhr.status === 200) {
        try {
          const student = JSON.parse(xhr.responseText);
          document.getElementById('detailId').textContent = student.id;
          document.getElementById('detailName').textContent = student.name;
          document.getElementById('detailAge').textContent = student.age;
          studentDetails.style.display = 'block';
        } catch (e) {
          showMessage('Error parsing response: ' + e.message, true);
          studentDetails.style.display = 'none';
        }
      } else if (xhr.status === 404) {
        showMessage('Student not found', true);
        studentDetails.style.display = 'none';
      } else {
        try {
          const response = JSON.parse(xhr.responseText);
          showMessage(response.error || 'Failed to retrieve student', true);
        } catch (e) {
          showMessage('Failed to retrieve student', true);
        }
        studentDetails.style.display = 'none';
      }
    };
    xhr.onerror = function() {
      showMessage('Network error occurred', true);
      document.getElementById('studentDetails').style.display = 'none';
    };
    xhr.send();
  }

  // 4. Update a student
  function updateStudent() {
    const id = document.getElementById('updateId').value.trim();
    const name = document.getElementById('updateName').value.trim();
    const age = document.getElementById('updateAge').value.trim();

    console.log("Update values - ID:", id, "Name:", name, "Age:", age);

    if (!id) {
      showMessage('Student ID is required', true);
      return;
    }

    if (!name && !age) {
      showMessage('At least one field (name or age) must be provided', true);
      return;
    }

    // 方法1: 尝试使用URL查询参数 - 更符合RESTful实践
    let url = '<%=request.getContextPath()%>/http-students?id=' + encodeURIComponent(id);
    if (name) url += '&name=' + encodeURIComponent(name);
    if (age) url += '&age=' + encodeURIComponent(age);

    console.log("Request URL:", url);

    const xhr = new XMLHttpRequest();
    xhr.open('PUT', url, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    xhr.onreadystatechange = function() {
      console.log("ReadyState:", xhr.readyState, "Status:", xhr.status);
      if (xhr.readyState === 4) {
        console.log("Response text:", xhr.responseText);

        if (xhr.status === 200) {
          showMessage('Student updated successfully');
          document.getElementById('updateId').value = '';
          document.getElementById('updateName').value = '';
          document.getElementById('updateAge').value = '';
          // Refresh the student list
          getAllStudents();
        } else if (xhr.status === 404) {
          showMessage('Student not found', true);
        } else {
          try {
            const response = JSON.parse(xhr.responseText);
            showMessage(response.error || 'Failed to update student', true);
          } catch (e) {
            showMessage('Failed to update student: ' + xhr.status, true);
          }
        }
      }
    };

    xhr.onerror = function() {
      showMessage('Network error occurred', true);
      console.error("XHR Error:", xhr);
    };

    xhr.send();
  }

  // 5. Delete a student
  function deleteStudent() {
    const id = document.getElementById('deleteId').value.trim();

    if (!id) {
      showMessage('Student ID is required', true);
      return;
    }

    if (!confirm('Are you sure you want to delete this student?')) {
      return;
    }

    const xhr = new XMLHttpRequest();
    xhr.open('DELETE', '<%=request.getContextPath()%>/http-students?id=' + encodeURIComponent(id), true);
    xhr.onload = function() {
      if (xhr.status === 200) {
        showMessage('Student deleted successfully');
        document.getElementById('deleteId').value = '';
        // Refresh the student list
        getAllStudents();
      } else if (xhr.status === 404) {
        showMessage('Student not found', true);
      } else {
        try {
          const response = JSON.parse(xhr.responseText);
          showMessage(response.error || 'Failed to delete student', true);
        } catch (e) {
          showMessage('Failed to delete student', true);
        }
      }
    };
    xhr.onerror = function() {
      showMessage('Network error occurred', true);
    };
    xhr.send();
  }

  // Load all students when page loads
  document.addEventListener('DOMContentLoaded', function() {
    getAllStudents();
  });
</script>
</body>
</html>