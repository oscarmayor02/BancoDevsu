function fn() {
    var env = karate.env; // obtener el ambiente java -Dkarate.env=qa
    karate.log('karate.env:', env);

    if (!env) {
        env = 'dev';
    }

    var config = {
        env: env,
        clienteServiceUrl: 'http://localhost:8081',
        cuentaServiceUrl: 'http://localhost:8082'
    }

    if (env == 'dev') {
        config.clienteServiceUrl = 'http://localhost:8081';
        config.cuentaServiceUrl = 'http://localhost:8082';
    } else if (env == 'docker') {
        config.clienteServiceUrl = 'http://cliente-service:8081';
        config.cuentaServiceUrl = 'http://cuenta-service:8082';
    }

    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);

    return config;
}