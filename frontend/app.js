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
        files.forEach((file, index) => {
          const li = document.createElement('li');
          li.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
          li.textContent = file;  // Assuming the API returns the file name

          // Create a download button for each file
          const downloadButton = document.createElement('a');
          downloadButton.classList.add('btn', 'btn-primary', 'btn-sm');
          downloadButton.textContent = 'Download';
          downloadButton.href = `/files/download/${index + 1}`;  // Assuming the file ID is its index + 1
          downloadButton.setAttribute('download', file);  // Set the download attribute to the file name

          li.appendChild(downloadButton);
          fileList.appendChild(li);
        });
      })
      .catch(error => {
        console.error('Error fetching file list:', error);
        uploadStatus.textContent = 'Error fetching file list: ' + error.message;
      });
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
        uploadStatus.textContent = 'File upload failed: ' + error.message;
        console.error('Error uploading file:', error);
      });
  });

  // Initial fetch of uploaded files when the page loads
  fetchUploadedFiles();
});