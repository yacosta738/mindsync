package io.mindsync.common.domain

import java.util.concurrent.ConcurrentHashMap

object Memoizers {
  fun <Result> of(supplier: () -> Result): () -> Result {
    return { of { _: Any? -> supplier() }(null) }
  }

  fun <Input, Result> of(function: (Input) -> Result): (Input) -> Result {
    return MemoizedFunction(function)
  }

  private class MemoizedFunction<Input, Result>(private val function: (Input) -> Result) : (Input) -> Result {
    private val results = ConcurrentHashMap<MemoizedInput<Input>, MemoizedResult<Result>>()

    override fun invoke(input: Input): Result {
      return results.computeIfAbsent(MemoizedInput(input), this::toMemoizedResult).result
    }

    private fun toMemoizedResult(input: MemoizedInput<Input>): MemoizedResult<Result> {
      return MemoizedResult(function(input.input))
    }

    private data class MemoizedInput<Input>(val input: Input)
    private data class MemoizedResult<Result>(val result: Result)
  }
}


