/* Copyright(C) 2014 - 2020 Momomo LTD. Proprietary and confidential. Usage of this file on any medium without a written consent by Momomo LTD. is strictly prohibited. All Rights Reserved. */
package momomo.com.annotations.informative;

/**
 * Declares that the intention was for the class to be non static, but for other reasons, such as retaining the ability to declare static classes, enums, or properties within the class lead to a need to make it static.
 * The class then require the parent usually to be passed from the parent class.
 *
 * So while the class is static, it is to be considered a non static innerclass in reality.
 *
 * @author Joseph S.
 */
public @interface NotStatic {}
