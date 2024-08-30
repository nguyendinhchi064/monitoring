document.addEventListener("DOMContentLoaded", function () {
    const token = localStorage.getItem('token');

    // Handle the Data Transfer Submission
    document.getElementById('submitDataTransfer').addEventListener('click', function (event) {
        event.preventDefault();

        const payload = JSON.parse(atob(token.split('.')[1]));
        const username = payload.username;

        const data = {
            mysqlHost: document.getElementById('mysqlHost').value,
            mysqlPort: document.getElementById('mysqlPort').value,
            mysqlDb: document.getElementById('mysqlDb').value,
            mysqlUser: document.getElementById('mysqlUser').value,
            mysqlPassword: document.getElementById('mysqlPassword').value,
            tableName: document.getElementById('tableName').value,
            mongoConnectionString: 'mongodb://root:root@localhost:27018/?authSource=admin',
            mongoDatabase: username,
            mongoCollection: document.getElementById('mongoCollection').value
        };

        $.ajax({
            url: '/db_transfer',
            type: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(data),
            success: function (response) {
                console.log('Job ID:', response);
                setTimeout(fetchJobStatuses, 1000);
            },
            error: function (xhr, status, error) {
                console.error('Error during data transfer:', error);
            }
        });
    });

    // Handle Refresh Button Click
    document.getElementById('refreshTable').addEventListener('click', function () {
        clearJobStatusTable();
        fetchJobStatuses();
    });

    function fetchJobStatuses() {
        const tableHead = document.getElementById('tableHead');
        const tableBody = document.getElementById('tableBody');

        if (!tableHead || !tableBody) {
            console.error('Table elements not found');
            return;
        }

        $.ajax({
            url: '/db_transfer/status',
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            success: function (data) {
                tableHead.innerHTML = '';
                tableBody.innerHTML = '';

                if (data.length > 0) {
                    const keys = Object.keys(data[0]);

                    // Create table headers
                    const headerRow = document.createElement('tr');
                    keys.forEach(key => {
                        const th = document.createElement('th');
                        th.textContent = key;
                        headerRow.appendChild(th);
                    });
                    tableHead.appendChild(headerRow);

                    // Create table rows
                    data.forEach(item => {
                        const row = document.createElement('tr');
                        keys.forEach(key => {
                            const td = document.createElement('td');
                            td.textContent = item[key];
                            row.appendChild(td);
                        });
                        tableBody.appendChild(row);
                    });

                    // Initialize or Refresh Simple-DataTables
                    if (tableBody.dataset.datatable) {
                        const dataTable = simpleDatatables.DataTable.instances.find(dt => dt.table === document.querySelector('#jobStatusTable'));
                        if (dataTable) {
                            dataTable.destroy();
                        }
                    }
                    new simpleDatatables.DataTable('#jobStatusTable');
                } else {
                    const noDataRow = document.createElement('tr');
                    const noDataCell = document.createElement('td');
                    noDataCell.colSpan = 1; // Assume a single column if no data
                    noDataCell.textContent = 'No data available';
                    noDataRow.appendChild(noDataCell);
                    tableBody.appendChild(noDataRow);
                }
            },
            error: function (xhr, status, error) {
                console.error('Error fetching job statuses:', error);
            }
        });
    }

    function clearJobStatusTable() {
        const tableHead = document.getElementById('tableHead');
        const tableBody = document.getElementById('tableBody');

        if (tableHead) tableHead.innerHTML = '';
        if (tableBody) tableBody.innerHTML = '';
    }

    fetchJobStatuses();
});
