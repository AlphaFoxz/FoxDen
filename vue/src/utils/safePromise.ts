async function safePromise<T, U = Error>(
  promise: Promise<T>,
  errorExt?: Record<string, any>,
): Promise<[T, null] | [null, U]> {
  try {
    const data = await promise;
    return [data, null];
  } catch (error_) {
    const errorObject = error_ as U;

    if (errorExt) {
      const parsedError = {
        ...(errorObject as Record<string, any>),
        ...errorExt,
      };

      return [null, parsedError as U];
    }

    return [null, errorObject];
  }
}

export default safePromise;
