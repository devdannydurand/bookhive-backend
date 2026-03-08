fetch(`http://localhost:8080/admin/usuarios`)
    .then(resp => resp.json())
    .then(data => console.log(data))
    .catch(error => console.error(error));


