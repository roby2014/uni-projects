/**
 * Creates an HTML element with the specified tag, attributes, and children.
 * 
 * @param {string} tag - The HTML tag name of the element to create.
 * @param {Promise<{}>|{}|string} attributes - The attributes to set on the element.
 *                                This can be a Promise resolving to an object, an object of attributes,
 *                                or a string representing a single attribute.
 * @param {...Promise<HTMLElement>|HTMLElement|string} children - The children elements or strings to append to the created element.
 *                                              These can be Promises resolving to HTMLElements, HTMLElements, or strings.
 * @returns {Promise<HTMLElement>} - A Promise that resolves to the created HTMLElement.
 */
async function createElement(tag, attributes, ...children) {
    const element = document.createElement(tag);
    attributes = await attributes; // the other attributes can also be a html dom promise

    if (isElement(attributes) || typeof attributes === "string")
        appendChild(element, attributes); // if its string or html dom tag, we add it to the root element 
    else if (attributes != null && typeof attributes === "object")
        setElementAttributes(element, attributes); // if its an object its element's attributes

    for (let child of children) {
        child = await child; // the children can also be promises

        // do the same as before, add children to the root element
        if (child != null && (isElement(child) || typeof child === "string"))
            appendChild(element, child);
    }

    return element;
}

/**
 * Sets the attributes of an HTML element.
 * 
 * @param {HTMLElement} element - The HTML element to set attributes on.
 * @param {Object} attributes - The attributes to set on the element.
 */
function setElementAttributes(element, attributes) {
    for (const attr in attributes) {
        if (attr == null)
            continue;

        const value = attributes[attr];
        if (value == null)
            continue;

        element.setAttribute(attr, value);
    }
}

/**
 * Appends a child element or text node to an HTML element.
 * 
 * @param {HTMLElement} element - The HTML element to append the child to.
 * @param {HTMLElement|string} child - The child element or text node to append.
 */
const appendChild = (element, child) => { element.appendChild(typeof child === "string" ? document.createTextNode(child) : child); };

/**
 * Checks if an object is an HTML element.
 * 
 * @param {Object} obj - The object to check.
 * @returns {boolean} - Returns true if the object is an HTML element, false otherwise.
 */
const isElement = (obj) => obj && typeof obj === "object" && obj.nodeType === 1 && typeof obj.nodeName === "string";

///
/// HTML DOM tags 
///

export const div = (attr, ...children) => createElement("div", attr, ...children);
export const form = (attr, ...children) => createElement("form", attr, ...children);
export const label = (attr, ...children) => createElement("label", attr, ...children);
export const input = (attr, ...children) => createElement("input", attr, ...children);
export const button = (attr, ...children) => createElement("button", attr, ...children);
export const a = (attr, ...children) => createElement("a", attr, ...children);
export const h1 = (attr, ...children) => createElement("h1", attr, ...children);
export const h2 = (attr, ...children) => createElement("h2", attr, ...children);
export const h3 = (attr, ...children) => createElement("h3", attr, ...children);
export const ul = (attr, ...children) => createElement("ul", attr, ...children);
export const li = (attr, ...children) => createElement("li", attr, ...children);
export const br = (attr, ...children) => createElement("br", attr, ...children);
