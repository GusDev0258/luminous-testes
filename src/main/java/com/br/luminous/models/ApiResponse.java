package com.br.luminous.models;

public record ApiResponse<T>(boolean success, String message, T data) {
}
