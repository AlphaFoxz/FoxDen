const easeInOutQuad = (t: number, b: number, c: number, d: number) => {
  t /= d / 2;
  if (t < 1) {
    return (c / 2) * t * t + b;
  }

  t--;
  return (-c / 2) * (t * (t - 2) - 1) + b;
};

// RequestAnimationFrame for Smart Animating http://goo.gl/sx5sts
const requestAnimFrame = (function () {
  return (
    globalThis.requestAnimationFrame
    || (globalThis as any).webkitRequestAnimationFrame
    || (globalThis as any).mozRequestAnimationFrame
    || function (callback) {
      globalThis.setTimeout(callback, 1000 / 60);
    }
  );
})();

/**
 * Because it's so fucking difficult to detect the scrolling element, just move them all
 * @param {number} amount
 */
const move = (amount: number) => {
  document.documentElement.scrollTop = amount;
  (document.body.parentNode as HTMLElement).scrollTop = amount;
  document.body.scrollTop = amount;
};

const position = () => document.documentElement.scrollTop || (document.body.parentNode as HTMLElement).scrollTop || document.body.scrollTop;

/**
 * @param {number} to
 * @param {number} duration
 * @param {Function} callback
 */
export const scrollTo = (to: number, duration: number, callback?: any) => {
  const start = position();
  const change = to - start;
  const increment = 20;
  let currentTime = 0;
  duration = duration === undefined ? 500 : duration;
  const animateScroll = function () {
    // Increment the time
    currentTime += increment;
    // Find the value with the quadratic in-out easing function
    const value = easeInOutQuad(currentTime, start, change, duration);
    // Move the document.body
    move(value);
    // Do the animation unless its over
    if (currentTime < duration) {
      requestAnimFrame(animateScroll);
    } else if (callback && typeof callback === 'function') {
      // The animation is done so lets callback
      callback();
    }
  };

  animateScroll();
};
