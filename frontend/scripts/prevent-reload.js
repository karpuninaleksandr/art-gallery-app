console.log("JavaScript file loaded");

window.addEventListener('beforeunload', function (event) {
    event.preventDefault();
    event.returnValue = '';
});

//window.addEventListener('beforeunload', function(event) {
//    // Отправить запрос на сервер
//    fetch('/handlePageReload', {
//        method: 'POST',
//        headers: {
//            'Content-Type': 'application/json',
//        },
//        body: JSON.stringify({ reload: true }), // Передаем информацию о перезагрузке
//    });
//});


//window.addEventListener('beforeunload', function (event) {
//    window.console.log("Window loaded");
    // Пример вызова метода decreaseLevel при загрузке страницы
//    if (typeof Vaadin !== 'undefined' && typeof Vaadin.Flow !== 'undefined' && Vaadin.Flow.clients) {
//            const client = Object.values(Vaadin.Flow.clients).find(client => client.server);
//            if (client) {
//                window.console.log("Calling decreaseLevel on server");
//                client.server.handlePageReload();
//            } else {
//                window.console.error("No Vaadin server client found");
//            }
//        } else {
//            window.console.error("Vaadin or Vaadin.Flow not found");
//        }
//});