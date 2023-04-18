package com.demoprojekt.webshop.service

import com.demoprojekt.webshop.exceptions.IdNotFoundException
import com.demoprojekt.webshop.exceptions.WebshopException
import com.demoprojekt.webshop.model.OrderCreateRequest
import com.demoprojekt.webshop.model.OrderPositionCreateRequest
import com.demoprojekt.webshop.model.OrderPositionResponse
import com.demoprojekt.webshop.model.OrderResponse
import com.demoprojekt.webshop.repository.CustomerRepositroy
import com.demoprojekt.webshop.repository.OrderPositionRepository
import com.demoprojekt.webshop.repository.OrderRepository
import com.demoprojekt.webshop.repository.ProductRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrderService(
        val productRepository: ProductRepository,
        val orderRepositroy: OrderRepository,
        val orderPositionRepositroy: OrderPositionRepository,
        val customerRepository: CustomerRepositroy
) {


    fun createOrder(request: OrderCreateRequest): OrderResponse {

        val customer = customerRepository.findById(request.customerId)
                ?: throw IdNotFoundException(message = "Customer with ${request.customerId} not found",
                        statusCode = HttpStatus.BAD_REQUEST)

        return orderRepositroy.save(request)
    }

    fun createNewPositionForOrder(orderId: String, request: OrderPositionCreateRequest): OrderPositionResponse {

        orderRepositroy.findById(orderId)
                ?: throw IdNotFoundException(message = "Order with $orderId not found",
                        statusCode = HttpStatus.BAD_REQUEST)

        if (productRepository.findById(request.productId).isEmpty)
            throw throw IdNotFoundException(message = "Product with ${request.productId} not found",
                    statusCode = HttpStatus.BAD_REQUEST)

        val orderPositionResponse = OrderPositionResponse(
                id = UUID.randomUUID().toString(),
                productId = request.productId,
                quantity = request.quantity
        )
        orderPositionRepositroy.save(orderPositionResponse)

        return orderPositionResponse
    }
}