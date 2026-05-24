package com.segurosbolivar.polizas.dto.response;

public final class ApiMessages {

    private ApiMessages() {}

    public static final String POLICY_FOUND     = "Política recuperada exitosamente";
    public static final String POLICIES_LISTED  = "Políticas enumeradas correctamente";
    public static final String POLICY_RENEWED   = "Política renovada con éxito";
    public static final String POLICY_CANCELLED = "Póliza cancelada exitosamente";
    public static final String POLICY_NOT_FOUND = "Estado de póliza no encontrado: ";
    public static final String RISK_FOUND       = "Riesgo recuperado con éxito";
    public static final String RISK_ADDED       = "Riesgo agregado exitosamente";
    public static final String RISK_CANCELLED   = "Riesgo cancelado con éxito";
    public static final String RISKS_LISTED     = "Riesgos enumerados con éxito";
    public static final String RISK_NOT_FOUND   = "Estado de riesgo no encontrado: ";
    public static final String CORE_NOTIFIED    = "Evento registrado en CORE";
    public static final String CORE_SEND        = "Evento enviado al CORE: ";
    public static final String USER_NOT_FOUND   = "Usuario asegurado no encontrado con id: ";
    public static final String POLICY_CANCELLED_ERROR = "No se puede renovar una póliza cancelada.";
    public static final String INVALID_TYPE     = "Solo las políticas colectivas pueden tener riesgos";
    public static final String INVALID_API_KEY  = "Acceso denegado: API Key inválida o ausente en ";
    public static final String CATALOG_NOT_ACTIVE  = "Estado ACTIVO no encontrado en catálogo";
    public static final String CATALOG_NOT_CANCEL  = "Estado CANCELADO no encontrado en catálogo";
    public static final String MSG_POLIZA_NO_ENCONTRADA = "Póliza no encontrada con id: ";
    public static final String MSG_RIESGO_NO_ENCONTRADO = "Riesgo no encontrado con id: ";
    public static final String MSG_SOLO_COLECTIVA = "Solo se pueden agregar riesgos a pólizas de tipo COLECTIVA";
    public static final String MSG_POLIZA_NO_ACTIVA = "No se pueden agregar riesgos a una póliza que no está activa";

    // Errores de estado de recurso
    public static final String MSG_RIESGO_YA_CANCELADO  = "El riesgo ya está cancelado";
    public static final String MSG_POLIZA_YA_CANCELADA  = "La póliza ya está cancelada";

    // Mensajes de GlobalExceptionHandler
    public static final String MSG_RUTA_NO_EXISTE       = "La ruta solicitada no existe: ";
    public static final String MSG_VALOR_NO_VALIDO      = "Valor no válido '%s' para el parámetro '%s'. Tipo esperado: %s";
    public static final String MSG_METODO_NO_SOPORTADO  = "Método HTTP '%s' no es compatible. Métodos compatibles: %s";
    public static final String MSG_BODY_INVALIDO        = "El cuerpo de la solicitud no está presente o contiene JSON no válido";
    public static final String MSG_PARAMETRO_FALTANTE   = "Parámetro requerido '%s' de tipo '%s' falta";
    public static final String MSG_INTEGRIDAD_DATOS     = "La operación no pudo completarse debido a una restricción de integridad de los datos";
    public static final String MSG_ERROR_INTERNO        = "El error no pudo ser gestionado dentro de las excepciones";
}
