// $(document).ready(function () {
//     const token = localStorage.getItem('token'); // Assuming the token is stored in localStorage
//
//     $.ajax({
//         url: '/db_transfer/status',
//         type: 'GET',
//         headers: {
//             'Authorization': `Bearer ${token}`
//         },
//         success: function (data) {
//             const tableHead = document.getElementById('tableHead');
//             const tableBody = document.getElementById('tableBody');
//
//             // Clear any existing data
//             tableHead.innerHTML = '';
//             tableBody.innerHTML = '';
//
//             if (data.length > 0) {
//                 // Extract keys from the first object for the table header
//                 const keys = Object.keys(data[0]);
//
//                 // Create the table headers
//                 const headerRow = document.createElement('tr');
//                 keys.forEach(key => {
//                     const th = document.createElement('th');
//                     th.textContent = key;
//                     headerRow.appendChild(th);
//                 });
//                 tableHead.appendChild(headerRow);
//
//                 // Create table rows
//                 data.forEach(item => {
//                     const row = document.createElement('tr');
//                     keys.forEach(key => {
//                         const td = document.createElement('td');
//                         td.textContent = item[key];
//                         row.appendChild(td);
//                     });
//                     tableBody.appendChild(row);
//                 });
//
//                 // Initialize the DataTable
//                 new simpleDatatables.DataTable('#jobStatusTable');
//             } else {
//                 // Handle the case where there's no data
//                 const noDataRow = document.createElement('tr');
//                 const noDataCell = document.createElement('td');
//                 noDataCell.colSpan = keys.length || 1;
//                 noDataCell.textContent = 'No data available';
//                 noDataRow.appendChild(noDataCell);
//                 tableBody.appendChild(noDataRow);
//             }
//         },
//         error: function (xhr, status, error) {
//             console.error('Error fetching job statuses:', error);
//         }
//     });
// });
