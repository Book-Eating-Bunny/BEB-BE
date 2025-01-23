package com.beb.backend.dto.responseDto;

import java.util.List;

public record WishlistBookListDto<T>(
        List<T> wishlistBooks
) {
}
