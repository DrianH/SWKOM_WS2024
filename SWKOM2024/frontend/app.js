document.addEventListener('DOMContentLoaded', () => {
  const uploadForm = document.getElementById('uploadForm');
  const fileInput = document.getElementById('fileInput');
  const uploadStatus = document.getElementById('uploadStatus');
  const fileList = document.getElementById('fileList');

  // Function to fetch and list uploaded files
  function fetchUploadedFiles() {
    fetch('/files/list')
      .then(response => response.json())
      .then(files => {
        fileList.innerHTML = ''; // Clear current list
        files.forEach(file => {
          const li = document.createElement('li');
          li.textContent = file;
          fileList.appendChild(li);
        });
      })
      .catch(error => console.error('Error fetching file list:', error));
  }

  // Handle file upload
  uploadForm.addEventListener('submit', (event) => {
    event.preventDefault();

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    fetch('/files/upload', {
      method: 'POST',
      body: formData,
    })
      .then(response => response.text())
      .then(message => {
        uploadStatus.textContent = message;
        fileInput.value = ''; // Clear file input after upload
        fetchUploadedFiles(); // Refresh the file list after upload
      })
      .catch(error => {
        uploadStatus.textContent = 'File upload failed: ' + error;
        console.error('Error uploading file:', error);
      });
  });

  // Initial fetch of uploaded files when the page loads
  fetchUploadedFiles();
});