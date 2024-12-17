package com.beb.backend.dto;

import java.util.List;

public record WishlistBooksResponseDto<T>(
        List<T> wishlistBooks
) {
}
