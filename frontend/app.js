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
          li.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');

          // Display ID and name
          li.textContent = `ID: ${file.id} - Name: ${file.name}`;

          // Create a download button for each file
          const downloadButton = document.createElement('a');
          downloadButton.classList.add('btn', 'btn-primary', 'btn-sm', 'mr-2');
          downloadButton.textContent = 'Download';
          downloadButton.href = `/files/download/${file.id}`;
          downloadButton.setAttribute('download', file.name);

          // Create a delete button for each file
          const deleteButton = document.createElement('button');
          deleteButton.classList.add('btn', 'btn-danger', 'btn-sm');
          deleteButton.textContent = 'Delete';
          deleteButton.addEventListener('click', () => {
            deleteFile(file.id);
          });

          // Append buttons to the list item
          li.appendChild(downloadButton);
          li.appendChild(deleteButton);

          fileList.appendChild(li);
        });
      })
      .catch(error => {
        console.error('Error fetching file list:', error);
        uploadStatus.textContent = 'Error fetching file list: ' + error.message;
      });
  }

  // Function to delete a file by ID
  function deleteFile(fileId) {
    fetch(`/files/delete/${fileId}`, {
      method: 'DELETE',
    })
      .then(response => {
        if (response.ok) {
          uploadStatus.textContent = `File with ID ${fileId} deleted successfully.`;
          fetchUploadedFiles(); // Refresh the file list
        } else {
          return response.text().then((message) => {
            throw new Error(message);
          });
        }
      })
      .catch(error => {
        uploadStatus.textContent = 'Error deleting file: ' + error.message;
        console.error('Error deleting file:', error);
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